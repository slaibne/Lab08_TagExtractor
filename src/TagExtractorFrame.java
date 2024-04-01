
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.List;

public class TagExtractorFrame extends JFrame {
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private Set<String> stopWordsList;
    private Map<String, Integer> numWordOccurances;

    public TagExtractorFrame() {
        setTitle("Tag Extractor");
        setSize(450, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        JButton openFileBtn = new JButton("Open File");
        JButton extractTagsBtn = new JButton("Extract Tags");
        JButton saveTagsBtn = new JButton("Save Tags");

        openFileBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });
        extractTagsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                extractTagsFromFile();
            }
        });
        saveTagsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveExtractedTags();
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(openFileBtn);
        btnPanel.add(extractTagsBtn);
        btnPanel.add(saveTagsBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        fileChooser = new JFileChooser();
        stopWordsList = loadStopWords("StopWords.txt");
        numWordOccurances = new HashMap<>();
    }

    private void openFile() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    processLine(line);
                }
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            textArea.setText("File loaded: " + file.getName());
        }
    }

    private void processLine(String line) {
        String[] words = line.split("\\s+");
        for (String word : words) {
            word = word.replaceAll("[^a-zA-Z]", "").toLowerCase();
            if (!stopWordsList.contains(word)) {
                int numOccurances = 1;
                if(numWordOccurances.get(word) != null) {
                    numOccurances = numWordOccurances.get(word) + 1;
                }
                numWordOccurances.put(word, numOccurances);
            }
        }
    }

    private Set<String> loadStopWords(String filename) {
        Set<String> stopWords = new HashSet<>();
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                stopWords.add(scanner.nextLine().toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopWords;
    }

    private void extractTagsFromFile() {
        List<Entry<String, Integer>> sortedTags = new ArrayList<>(numWordOccurances.entrySet());
        sortedTags.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        StringBuilder tagsText = new StringBuilder();
        for (Entry<String, Integer> entry : sortedTags) {
            tagsText.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        textArea.setText(tagsText.toString());
    }

    private void saveExtractedTags() {
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(file)) {
                for (Entry<String, Integer> entry : numWordOccurances.entrySet()) {
                    writer.println(entry.getKey() + ": " + entry.getValue());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
