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

public class ReTreatment {

	public static void main(String[] args) throws Exception {
		 String path = "data/raw/online_shopping_10_cats.csv";

	        int total = 0;
	        int emptyReview = 0;
	        int duplicate = 0;
	        int validCount = 0;

	        Set<String> seen = new HashSet<>();
	        Map<String, Integer> labelDist = new LinkedHashMap<>();
	        Map<String, Integer> catDist = new LinkedHashMap<>();
	        List<Integer> lengths = new ArrayList<>();
	        long totalLen = 0;

	        try (CSVReader reader = new CSVReaderBuilder(
	                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))
	                .withSkipLines(1)
	                .build()) {

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

	                if (review.isEmpty()) {
	                    emptyReview++;
	                    continue;
	                }
	                if (!seen.add(review)) {
	                    duplicate++;
	                    continue;
	                }

	                validCount++;
	                labelDist.merge(label, 1, Integer::sum);
	                catDist.merge(cat, 1, Integer::sum);
	                totalLen += review.length();
	                lengths.add(review.length());
	            }
	        }

	        Collections.sort(lengths);

	        System.out.println("===== 数据体检结果 =====");
	        System.out.println("总行数(不含表头): " + total);
	        System.out.println("空评论文本: " + emptyReview);
	        System.out.println("重复评论(去重): " + duplicate);
	        System.out.println("有效唯一评论数: " + validCount);
	        System.out.println("标签分布: " + labelDist);
	        System.out.println("类别分布: " + catDist);
	        System.out.println("平均文本长度: " + (validCount == 0 ? 0 : (totalLen * 1.0 / validCount)));
	        if (!lengths.isEmpty()) {
	            System.out.println("长度中位数: " + lengths.get(lengths.size() / 2));
	            System.out.println("最短长度: " + lengths.get(0));
	            System.out.println("最长长度: " + lengths.get(lengths.size() - 1));
	        }
	}
}