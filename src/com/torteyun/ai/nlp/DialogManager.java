package com.torteyun.ai.nlp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//这个是对话管理类，负责处理对话的逻辑

/*
 * @author torteyun
 * @version 1.0
 * @since 2025-05-01
 */


public class DialogManager {
    // 对话状态
    private enum DialogState {
        INITIAL,        // 初始状态
        GREETING,       // 问候状态
        UNDERSTANDING,  // 理解状态
        PROCESSING,     // 处理状态
        RESPONDING,     // 响应状态
        WAITING,        // 等待状态
        CLOSING         // 结束状态
    }
    
    // 当前对话状态
    private DialogState currentState;
    
    // 对话历史
    private List<String> dialogHistory;
    
    // 对话上下文
    private Map<String, Object> dialogContext;
    
    // 自然语言处理组件
    private NLPProcessor nlpProcessor;
    
    // 知识图谱服务
    private com.torteyun.ai.knowledege.KnowledgeGraphService knowledgeService;
    
    // 深度思考组件
    private com.torteyun.ai.deepthinking.deepthinking deepThinking;
    
    // 机器学习组件
    private com.torteyun.ai.ml.RobotLearning robotLearning;
    
    /**
     * 默认构造函数
     */
    public DialogManager() {
        this.currentState = DialogState.INITIAL;
        this.dialogHistory = new ArrayList<>();
        this.dialogContext = new HashMap<>();
        this.nlpProcessor = new NLPProcessor();
        this.knowledgeService = new com.torteyun.ai.knowledege.KnowledgeGraphService();
        this.deepThinking = new com.torteyun.ai.deepthinking.deepthinking();
        this.robotLearning = new com.torteyun.ai.ml.RobotLearning();
    }
    
    /**
     * 处理用户输入
     * 
     * @param userInput 用户输入的文本
     * @return 系统响应
     */
    public String processInput(String userInput) {
        // 记录对话历史
        dialogHistory.add("用户: " + userInput);
        
        // 根据当前状态处理输入
        String response = "";
        switch (currentState) {
            case INITIAL:
                response = handleInitialState(userInput);
                break;
            case GREETING:
                response = handleGreetingState(userInput);
                break;
            case UNDERSTANDING:
                response = handleUnderstandingState(userInput);
                break;
            case PROCESSING:
                response = handleProcessingState(userInput);
                break;
            case RESPONDING:
                response = handleRespondingState(userInput);
                break;
            case WAITING:
                response = handleWaitingState(userInput);
                break;
            case CLOSING:
                response = handleClosingState(userInput);
                break;
            default:
                response = "对不起，系统当前状态异常，请稍后再试。";
                currentState = DialogState.INITIAL;
        }
        
        // 记录系统响应
        dialogHistory.add("系统: " + response);
        
        return response;
    }
    
    /**
     * 处理初始状态
     */
    private String handleInitialState(String userInput) {
        // 分析用户意图
        Map<String, Object> intent = nlpProcessor.analyzeIntent(userInput);
        
        // 根据意图转换状态
        if (intent.containsKey("greeting")) {
            currentState = DialogState.GREETING;
            return "您好！我是TorteAI，有什么可以帮助您的吗？";
        } else if (intent.containsKey("question")) {
            currentState = DialogState.UNDERSTANDING;
            return "我正在理解您的问题...";
        } else if (intent.containsKey("command")) {
            currentState = DialogState.PROCESSING;
            return "我正在处理您的请求...";
        } else {
            return "您好！请问有什么可以帮助您的吗？";
        }
    }
    
    /**
     * 处理问候状态
     */
    private String handleGreetingState(String userInput) {
        // 分析用户情感
        double sentiment = nlpProcessor.analyzeSentiment(userInput);
        
        // 更新对话上下文
        dialogContext.put("userSentiment", sentiment);
        
        // 转换到理解状态
        currentState = DialogState.UNDERSTANDING;
        
        if (sentiment > 0.5) {
            return "很高兴见到您！请问有什么我可以帮助您的？";
        } else if (sentiment < -0.5) {
            return "看起来您心情不太好，有什么我能帮助您改善的吗？";
        } else {
            return "我能为您做些什么呢？";
        }
    }
    
    /**
     * 处理理解状态
     */
    private String handleUnderstandingState(String userInput) {
        // 提取关键实体
        List<String> entities = nlpProcessor.extractEntities(userInput);
        
        // 更新对话上下文
        dialogContext.put("entities", entities);
        
        // 查询知识图谱
        List<com.torteyun.ai.knowledege.KnowledgeGraphService.Fact> facts = new ArrayList<>();
        for (String entity : entities) {
            facts.addAll(knowledgeService.queryFactsByEntity(entity));
        }
        
        // 更新对话上下文
        dialogContext.put("facts", facts);
        
        // 转换到处理状态
        currentState = DialogState.PROCESSING;
        
        return "我已理解您的问题，正在思考中...";
    }
    
    /**
     * 处理处理状态
     */
    private String handleProcessingState(String userInput) {
        // 使用深度思考组件进行推理
        deepThinking.setThinkingDepth(3);
        List<String> thinkingResults = deepThinking.getThinkingResults();
        
        // 更新对话上下文
        dialogContext.put("thinkingResults", thinkingResults);
        
        // 转换到响应状态
        currentState = DialogState.RESPONDING;
        
        return "我已经完成了思考，正在准备回答...";
    }
    
    /**
     * 处理响应状态
     */
    private String handleRespondingState(String userInput) {
        // 生成响应
        String response = generateResponse();
        
        // 转换到等待状态
        currentState = DialogState.WAITING;
        
        return response;
    }
    
    /**
     * 处理等待状态
     */
    private String handleWaitingState(String userInput) {
        // 分析用户意图
        Map<String, Object> intent = nlpProcessor.analyzeIntent(userInput);
        
        // 根据意图转换状态
        if (intent.containsKey("farewell")) {
            currentState = DialogState.CLOSING;
            return "您是想结束我们的对话吗？";
        } else if (intent.containsKey("question")) {
            currentState = DialogState.UNDERSTANDING;
            return "我正在理解您的新问题...";
        } else {
            return "还有其他问题我可以帮助您解答吗？";
        }
    }
    
    /**
     * 处理结束状态
     */
    private String handleClosingState(String userInput) {
        // 学习对话经验
        Map<String, Object> learningData = new HashMap<>();
        learningData.put("dialogHistory", dialogHistory);
        learningData.put("dialogContext", dialogContext);
        
        List<Map<String, Object>> trainingData = new ArrayList<>();
        trainingData.add(learningData);
        
        robotLearning.startLearning(trainingData);
        
        // 重置状态
        currentState = DialogState.INITIAL;
        
        return "感谢您的交流！希望我的回答对您有所帮助。再见！";
    }
    
    /**
     * 生成响应
     */
    private String generateResponse() {
        // 获取对话上下文中的思考结果
        @SuppressWarnings("unchecked")
        List<String> thinkingResults = (List<String>) dialogContext.getOrDefault("thinkingResults", new ArrayList<String>());
        
        if (thinkingResults.isEmpty()) {
            return "对不起，我目前没有足够的信息来回答您的问题。";
        }
        
        // 简单拼接思考结果作为响应
        StringBuilder response = new StringBuilder();
        response.append("根据我的分析：\n");
        
        for (int i = 0; i < Math.min(3, thinkingResults.size()); i++) {
            response.append("- ").append(thinkingResults.get(i)).append("\n");
        }
        
        return response.toString();
    }
    
    /**
     * 获取对话历史
     * 
     * @return 对话历史记录
     */
    public List<String> getDialogHistory() {
        return this.dialogHistory;
    }
    
    /**
     * 获取当前对话状态
     * 
     * @return 当前状态
     */
    public DialogState getCurrentState() {
        return this.currentState;
    }
    
    /**
     * 重置对话
     */
    public void resetDialog() {
        this.currentState = DialogState.INITIAL;
        this.dialogContext.clear();
        // 保留历史记录用于学习
    }
    
    /**
     * 自然语言处理器内部类
     */
    private class NLPProcessor {
        /**
         * 分析用户意图
         * 
         * @param text 用户输入文本
         * @return 意图分析结果
         */
        public Map<String, Object> analyzeIntent(String text) {
            Map<String, Object> intent = new HashMap<>();
            
            // 简单规则匹配
            if (text.contains("你好") || text.contains("早上好") || text.contains("晚上好")) {
                intent.put("greeting", 0.9);
            }
            
            if (text.contains("再见") || text.contains("拜拜") || text.contains("结束")) {
                intent.put("farewell", 0.9);
            }
            
            if (text.contains("?") || text.contains("？") || text.contains("什么") || text.contains("如何") || text.contains("为什么")) {
                intent.put("question", 0.8);
            }
            
            if (text.contains("请") || text.startsWith("帮我") || text.contains("能否")) {
                intent.put("command", 0.7);
            }
            
            return intent;
        }
        
        /**
         * 分析情感
         * 
         * @param text 用户输入文本
         * @return 情感得分（-1到1，负面到正面）
         */
        public double analyzeSentiment(String text) {
            // 简单情感分析
            double score = 0.0;
            
            // 正面词汇
            String[] positiveWords = {"好", "喜欢", "棒", "优秀", "感谢", "谢谢", "开心", "高兴"};
            for (String word : positiveWords) {
                if (text.contains(word)) {
                    score += 0.2;
                }
            }
            
            // 负面词汇
            String[] negativeWords = {"不", "差", "糟", "失望", "生气", "讨厌", "烦", "坏"};
            for (String word : negativeWords) {
                if (text.contains(word)) {
                    score -= 0.2;
                }
            }
            
            return Math.max(-1.0, Math.min(1.0, score));
        }
        
        /**
         * 提取实体
         * 
         * @param text 用户输入文本
         * @return 提取的实体列表
         */
        public List<String> extractEntities(String text) {
            List<String> entities = new ArrayList<>();
            
            // 简单的实体提取
            String[] words = text.split("\\s+");
            for (String word : words) {
                if (word.length() > 1) {
                    entities.add(word);
                }
            }
            
            return entities;
        }
    }
}
