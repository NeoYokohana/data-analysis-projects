# 电商评论情感分析与主题挖掘

基于 62,774 条中文电商评论（online_shopping_10_cats，10 个商品类目）的文本挖掘全流程。

## 结果

| 指标 | 数值 |
|---|---|
| 原始数据 | 62,774 条 |
| 清洗后有效语料 | 61,835 条（保留率 98.5%） |
| 训练 / 测试 | 49,468 / 12,367（8:2，固定种子 42） |
| 词汇特征 | 57,101 维 |
| 情感分类准确率 | **89.57%** |
| F1 | **0.8979** |
| LDA 主题数 | 5（迭代 500 次） |

关键发现：交叉分析显示「售后客服」是唯一负面占比过半的主题，可支撑差评预警与工单排序。

## 流程

1. `ReTreatment.java` —— 数据体检（总量、标签分布、文本长度）
2. `Preprocess.java` —— 正则清洗、去重、Jieba 分词、停用词过滤 → `output/processed.csv`
3. `SentimentAnalysis.java` —— 手写多项式朴素贝叶斯（HashMap 概率矩阵 + 拉普拉斯平滑）→ 混淆矩阵与评估指标
4. `TopicModeling.java` —— Mallet LDA 主题建模 → 各主题 Top10 词

## 运行

```bash
# 需要 JDK 8+ 与 Maven
mvn compile

# 数据放在 data/raw/online_shopping_10_cats.csv
# 数据来源：https://github.com/SophonPlus/ChineseNlpCorpus
java -cp target/classes FinalWork.ReTreatment
java -cp target/classes FinalWork.Preprocess
java -cp target/classes FinalWork.SentimentAnalysis
java -cp target/classes FinalWork.TopicModeling
```

## 说明

课程项目，按课程要求使用 AI 工具辅助编码，并在每个源文件头部标注了工具与版本；方法选型、清洗规则、参数设定与结果解读由本人完成。

报告见 [report.pdf](./report.pdf)。
