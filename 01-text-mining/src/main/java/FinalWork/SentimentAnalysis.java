/*
 * 本程序及代码是在人工智能工具辅助下完成的。
 * 工具名称：DeepSeek
 * 版本/型号：V3
 * 开发机构/公司：深度求索
 * 版本颁布日期：2024-12
 */

package FinalWork;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SentimentAnalysis {

    static int TP = 0, TN = 0, FP = 0, FN = 0;

    public static void main(String[] args) throws Exception {
        String inputPath = "output/processed.csv";
        
        List<String[]> data = new ArrayList<>();
        try (CSVReader reader = new CSVReaderBuilder(
                new InputStreamReader(new FileInputStream(inputPath), StandardCharsets.UTF_8))
                .withSkipLines(1).build()) {
            String[] row;
            while ((row = reader.readNext()) != null) {
                data.add(row);
            }
        }

        Collections.shuffle(data, new Random(42));
        int splitIndex = (int) (data.size() * 0.8);
        List<String[]> trainData = data.subList(0, splitIndex);
        List<String[]> testData = data.subList(splitIndex, data.size());

        Map<String, Map<String, Integer>> wordCounts = new HashMap<>();
        Map<String, Integer> classCounts = new HashMap<>();
        Set<String> vocabulary = new HashSet<>();

        for (String[] row : trainData) {
            String label = row[1];
            String[] words = row[3].split(" ");
            classCounts.merge(label, 1, Integer::sum);
            wordCounts.putIfAbsent(label, new HashMap<>());
            
            for (String word : words) {
                vocabulary.add(word);
                wordCounts.get(label).merge(word, 1, Integer::sum);
            }
        }

        int correct = 0;
        for (String[] row : testData) {
            String trueLabel = row[1];
            String[] words = row[3].split(" ");
            
            String predictLabel = predict(words, wordCounts, classCounts, vocabulary, trainData.size());
            
            if (predictLabel.equals(trueLabel)) {
                correct++;
                if (trueLabel.equals("1")) TP++; else TN++;
            } else {
                if (trueLabel.equals("1")) FN++; else FP++;
            }
        }

        double accuracy = (double) correct / testData.size();
        double precision = (double) TP / (TP + FP);
        double recall = (double) TP / (TP + FN);
        double f1 = 2 * precision * recall / (precision + recall);

        System.out.println("===== 朴素贝叶斯情感分类结果 =====");
        System.out.println("训练集大小: " + trainData.size());
        System.out.println("测试集大小: " + testData.size());
        System.out.println("词表大小: " + vocabulary.size());
        System.out.println("---------------------------------");
        System.out.printf("准确率 (Accuracy): %.2f%%\n", accuracy * 100);
        System.out.printf("精确率 (Precision): %.2f%%\n", precision * 100);
        System.out.printf("召回率 (Recall): %.2f%%\n", recall * 100);
        System.out.printf("F1 分数 (F1-Score): %.4f\n", f1);
        System.out.println("---------------------------------");
        System.out.println("混淆矩阵:");
        System.out.println("           预测=1   预测=0");
        System.out.println("实际=1 (TP) " + TP + "    (FN) " + FN);
        System.out.println("实际=0 (FP) " + FP + "    (TN) " + TN);
    }

    private static String predict(String[] words, Map<String, Map<String, Integer>> wordCounts, 
                                  Map<String, Integer> classCounts, Set<String> vocab, int totalTrainSize) {
        double bestScore = Double.NEGATIVE_INFINITY;
        String bestLabel = "0";
        
        double alpha = 1.0;
        double vSize = vocab.size();

        for (String label : classCounts.keySet()) {
            double logProb = Math.log((double) classCounts.get(label) / totalTrainSize);
            Map<String, Integer> counts = wordCounts.get(label);
            int totalWordsInClass = counts.values().stream().mapToInt(Integer::intValue).sum();

            for (String word : words) {
                if (!vocab.contains(word)) continue;
                int count = counts.getOrDefault(word, 0);
                logProb += Math.log((count + alpha) / (totalWordsInClass + alpha * vSize));
            }

            if (logProb > bestScore) {
                bestScore = logProb;
                bestLabel = label;
            }
        }
        return bestLabel;
    }
}