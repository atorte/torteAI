package com.torteyun.ai.deepthinking;

import java.util.ArrayList;
import java.util.List;

//这个是深度思考类，负责处理深度思考的逻辑
//参考 DeepSeek 设计，包含推理链、自反思、上下文记忆、动态提示等能力

/*
 * @author torteyun
 * @version 1.0
 * @since 2025-05-01
 */ 

public class deepthinking {
    // 深度思考模式
    private enum ThinkingMode {
        CHAIN_OF_THOUGHT,    // 思维链
        TREE_OF_THOUGHT,     // 思维树
        REFLECTION,          // 反思模式
        EXPLORATION          // 探索模式
    }
    public int getThinkingDepth() {
        return thinkingDepth;
    }
    
    public void setThinkingDepth(int thinkingDepth) {
        this.thinkingDepth = thinkingDepth;
    }
    
    public ThinkingMode getCurrentMode() {
        return currentMode;
    }
    
    public void setCurrentMode(ThinkingMode currentMode) {
        this.currentMode = currentMode;
    }


    public int getThinkingWidth() {
        return thinkingWidth;
    }

    public void setThinkingWidth(int thinkingWidth) {
        this.thinkingWidth = thinkingWidth;
    }
    
    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public com.torteyun.ai.deeplearning.DeepLearning getDeepLearningModel() {
        return deepLearningModel;
    }

    public void setDeepLearningModel(com.torteyun.ai.deeplearning.DeepLearning deepLearningModel) {
        this.deepLearningModel = deepLearningModel;
    }

    public List<String> getThinkingResults() {
        return thinkingResults;
    }

    public void setThinkingResults(List<String> thinkingResults) {
        this.thinkingResults = thinkingResults;
    }

    public List<List<String>> getThinkingPaths() {
        return thinkingPaths;
    }


    public List<String> getKnowledgeEntities() {
        return knowledgeEntities;
    }
    
    public void setKnowledgeEntities(List<String> knowledgeEntities) {
        this.knowledgeEntities = knowledgeEntities;
    }






    // 当前思考模式
    private ThinkingMode currentMode;
    
    // 思考深度
    private int thinkingDepth;
    
    // 思考宽度（用于思维树模式）
    private int thinkingWidth;
    
    // 思考温度（控制思考发散程度）
    private double temperature;
    
    // 深度学习组件
    private com.torteyun.ai.deeplearning.DeepLearning deepLearningModel;
    
    // 思考结果
    private List<String> thinkingResults;
    
    // 思考路径（用于思维树模式）
    private List<List<String>> thinkingPaths;
    
    // 知识实体
    private List<String> knowledgeEntities;
    
    // 推理链：记录每一步的推理过程
    private List<String> reasoningChain;
    
    // 上下文记忆：存储历史推理内容
    private List<String> contextMemory;
    
    /**
     * 默认构造函数
     */
    public deepthinking() {
        this.currentMode = ThinkingMode.CHAIN_OF_THOUGHT;
        this.thinkingDepth = 5;
        this.thinkingWidth = 3;
        this.temperature = 0.7;
        this.deepLearningModel = new com.torteyun.ai.deeplearning.DeepLearning();
        this.thinkingResults = new ArrayList<>();
        this.thinkingPaths = new ArrayList<>();
        this.knowledgeEntities = new ArrayList<>();
        this.reasoningChain = new ArrayList<>();
        this.contextMemory = new ArrayList<>();
    }
    
    /**
     * 带参数的构造函数
     * 
     * @param mode 思考模式
     * @param depth 思考深度
     * @param width 思考宽度
     * @param temp 思考温度
     */
    public deepthinking(ThinkingMode mode, int depth, int width, double temp) {
        this.currentMode = mode;
        this.thinkingDepth = depth;
        this.thinkingWidth = width;
        this.temperature = temp;
        this.deepLearningModel = new com.torteyun.ai.deeplearning.DeepLearning();
        this.thinkingResults = new ArrayList<>();
        this.thinkingPaths = new ArrayList<>();
        this.knowledgeEntities = new ArrayList<>();
        this.reasoningChain = new ArrayList<>();
        this.contextMemory = new ArrayList<>();
    }
   

    // 添加推理步骤
    public void addReasoningStep(String step) {
        reasoningChain.add(step);
        contextMemory.add(step);
    }

    // 获取当前推理链
    public List<String> getReasoningChain() {
        return reasoningChain;
    }

    // 自反思：对当前推理链进行反思和修正
    public String selfReflect() {
        // 简单示例：拼接所有推理步骤，后续可接入大模型进行自反思
        StringBuilder reflection = new StringBuilder("反思：\n");
        for (String step : reasoningChain) {
            reflection.append(step).append("\n");
        }
        // 这里可以加入更复杂的自反思逻辑
        return reflection.toString();
    }

    // 动态提示生成：根据上下文生成提示词
    public String generateDynamicPrompt() {
        // 简单示例：取最近的上下文内容
        if (contextMemory.isEmpty()) return "";
        return "请基于以下内容继续推理：" + contextMemory.get(contextMemory.size() - 1);
    }

    // 清空推理链和上下文
    public void reset() {
        reasoningChain.clear();
        contextMemory.clear();
    }
}


