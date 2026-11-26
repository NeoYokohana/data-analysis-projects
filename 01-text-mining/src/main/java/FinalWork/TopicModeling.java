/*
 * 本程序及代码是在人工智能工具辅助下完成的。
 * 工具名称：DeepSeek
 * 版本/型号：V3
 * 开发机构/公司：深度求索
 * 版本颁布日期：2024-12
 */

package FinalWork;

import cc.mallet.pipe.*;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class TopicModeling {
    public static void main(String[] args) throws Exception {
        String inputPath = "output/processed.csv";

        ArrayList<Pipe> pipeList = new ArrayList<>();
        
        pipeList.add(new CharSequence2TokenSequence(Pattern.compile("\\S+")));
        pipeList.add(new TokenSequence2FeatureSequence());

        InstanceList instances = new InstanceList(new SerialPipes(pipeList));

        try (CSVReader reader = new CSVReaderBuilder(
                new InputStreamReader(new FileInputStream(inputPath), StandardCharsets.UTF_8))
                .withSkipLines(1).build()) {
            String[] row;
            int count = 0;
            while ((row = reader.readNext()) != null) {
                if (row.length > 3 && !row[3].trim().isEmpty()) {
                    String segmentedText = row[3];
                    instances.addThruPipe(new Instance(segmentedText, null, String.valueOf(count), null));
                    count++;
                }
            }
            System.out.println("成功加载数据: " + count + " 条");
        }

        int numTopics = 5; 
        ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);
        model.addInstances(instances);
        model.setNumThreads(4); 
        model.setNumIterations(500); 

        System.out.println("正在训练 LDA 模型，请稍候...");
        model.estimate();

        System.out.println("\n词表大小 (Alphabet size): " + model.getAlphabet().size());
        System.out.println("===== LDA 主题挖掘结果 =====");
        
        Object[][] topWordsMatrix = model.getTopWords(10);
        
        for (int t = 0; t < numTopics; t++) {
            System.out.printf("主题 %d: ", t + 1);
            Object[] words = topWordsMatrix[t];
            for (Object word : words) {
                System.out.print(word + " ");
            }
            System.out.println();
        }
    }
}
