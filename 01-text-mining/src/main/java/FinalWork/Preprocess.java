/*
 * 本程序及代码是在人工智能工具辅助下完成的。
 * 工具名称：DeepSeek
 * 版本/型号：V3
 * 开发机构/公司：深度求索
 * 版本颁布日期：2024-12
 */

package FinalWork;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class Preprocess {
    public static void main(String[] args) throws Exception {
        String inputPath = "data/raw/online_shopping_10_cats.csv";
        String outputPath = "output/processed.csv";
        String freqPath = "output/word_freq.csv";

        new File("output").mkdirs();

        Set<String> stopwords = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                Preprocess.class.getResourceAsStream("/stopwords.txt"), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) stopwords.add(line);
            }
        }

        JiebaSegmenter segmenter = new JiebaSegmenter();
        Set<String> seen = new HashSet<>();
        Map<String, Integer> wordFreq = new HashMap<>();

        int total = 0, empty = 0, duplicate = 0, tooShort = 0, kept = 0;

        try (CSVReader reader = new CSVReaderBuilder(
                new InputStreamReader(new FileInputStream(inputPath), StandardCharsets.UTF_8))
                .withSkipLines(1).build();
             CSVWriter writer = new CSVWriter(new OutputStreamWriter(
                     new FileOutputStream(outputPath), StandardCharsets.UTF_8))) {

            writer.writeNext(new String[]{"cat", "label", "review", "segmented"});

            String[] row;
            boolean first = true;
            while ((row = reader.readNext()) != null) {
                total++;
                if (row.length < 3) continue;

                String cat = row[0].trim();
                String label = row[1].trim();
                String review = row[2].trim();

                if (first) {
                    if (cat.startsWith("\uFEFF")) cat = cat.substring(1);
                    first = false;
                }

                if (review.isEmpty()) { empty++; continue; }

                String cleaned = review
                        .replaceAll("<[^>]+>", " ")
                        .replaceAll("http\\S+|www\\.\\S+", " ")
                        .replaceAll("@\\w+", " ")
                        .replaceAll("#[^#]+#", " ")
                        .replaceAll("[^\\u4e00-\\u9fa5a-zA-Z0-9]", " ")
                        .replaceAll("\\s+", " ")
                        .trim();

                if (cleaned.length() < 3) { tooShort++; continue; }
                if (!seen.add(cleaned)) { duplicate++; continue; }

                List<String> tokens = segmenter.sentenceProcess(cleaned);
                List<String> words = new ArrayList<>();
                for (String t : tokens) {
                    t = t.trim();
                    if (t.length() < 2) continue;
                    if (stopwords.contains(t)) continue;
                    if (t.matches("\\d+")) continue;
                    words.add(t);
                    wordFreq.merge(t, 1, Integer::sum);
                }

                if (words.size() < 2) { tooShort++; continue; }

                kept++;
                writer.writeNext(new String[]{cat, label, cleaned, String.join(" ", words)});
            }
        }

        List<Map.Entry<String, Integer>> freqList = new ArrayList<>(wordFreq.entrySet());
        freqList.sort((a, b) -> b.getValue() - a.getValue());

        try (CSVWriter fw = new CSVWriter(new OutputStreamWriter(
                new FileOutputStream(freqPath), StandardCharsets.UTF_8))) {
            fw.writeNext(new String[]{"word", "freq"});
            for (int i = 0; i < Math.min(50, freqList.size()); i++) {
                fw.writeNext(new String[]{freqList.get(i).getKey(),
                        String.valueOf(freqList.get(i).getValue())});
            }
        }

        System.out.println("===== 预处理结果 =====");
        System.out.println("总行数: " + total);
        System.out.println("空文本: " + empty);
        System.out.println("清洗后过短: " + tooShort);
        System.out.println("重复: " + duplicate);
        System.out.println("保留文档数: " + kept);
        System.out.println("词汇量: " + wordFreq.size());
        System.out.println("Top10词: ");
        for (int i = 0; i < Math.min(10, freqList.size()); i++) {
            System.out.println("  " + freqList.get(i).getKey() + " : " + freqList.get(i).getValue());
        }
    }
}