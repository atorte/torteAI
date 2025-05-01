package com.torteyun.ai.bot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.net.URI;




/*
 * @author torteyun
 * @version 1.0
 * @since 2025-05-01
 */


public class spider {
   
    // 爬虫配置参数
    private String userAgent;
    private int maxPages;
    private int timeout;
    private Set<String> visitedUrls;
    private Queue<String> urlQueue;
    private ExecutorService executorService;
    
    // 数据存储
    private List<String> crawledData;
    
    /**
     * 构造函数
     */
    public spider() {
        this("Mozilla/5.0 (compatible; TorteYunBot/1.0)", 3, 1000, 5000);
    }
    
    /**
     * 带参数的构造函数
     * 
     * @param userAgent 用户代理
     * @param maxDepth 最大爬取深度
     * @param maxPages 最大爬取页面数
     * @param timeout 超时时间(毫秒)
     */
    public spider(String userAgent, int maxDepth, int maxPages, int timeout) {
        this.userAgent = userAgent;
        this.maxPages = maxPages;
        this.timeout = timeout;
        this.visitedUrls = new HashSet<>();
        this.urlQueue = new LinkedList<>();
        this.crawledData = new ArrayList<>();
        this.executorService = Executors.newFixedThreadPool(10);
    }
    
    /**
     * 开始爬取数据
     * 
     * @param seedUrls 种子URL列表
     * @return 爬取的数据
     */
    public List<String> crawl(List<String> seedUrls) {
        // 添加种子URL到队列
        urlQueue.addAll(seedUrls);
        
        int pageCount = 0;
        
        while (!urlQueue.isEmpty() && pageCount < maxPages) {
            String url = urlQueue.poll();
            
            if (visitedUrls.contains(url)) {
                continue;
            }
            
            try {
                // 爬取页面内容
                String content = fetchContent(url);
                if (content != null) {
                    // 处理页面内容
                    processContent(url, content);
                    pageCount++;
                    
                    // 提取新的URL
                    List<String> newUrls = extractUrls(url, content);
                    for (String newUrl : newUrls) {
                        if (!visitedUrls.contains(newUrl)) {
                            urlQueue.add(newUrl);
                        }
                    }
                }
                
                // 标记为已访问
                visitedUrls.add(url);
                
                // 休眠一段时间，避免请求过于频繁
                Thread.sleep(1000);
                
            } catch (Exception e) {
                System.err.println("爬取URL时出错: " + url + " - " + e.getMessage());
            }
        }
        
        // 关闭线程池
        executorService.shutdown();
        
        return crawledData;
    }
    
    /**
     * 异步爬取数据
     * 
     * @param seedUrls 种子URL列表
     * @param callback 回调函数
     */
    public void crawlAsync(List<String> seedUrls, Consumer<List<String>> callback) {
        executorService.submit(() -> {
            List<String> result = crawl(seedUrls);
            callback.accept(result);
        });
    }
    
    /**
     * 获取页面内容
     * 
     * @param url 页面URL
     * @return 页面内容
     */
    private String fetchContent(String url) {
        HttpURLConnection connection = null;
        try {
            java.net.URI uriObj = new java.net.URI(url);
            URL urlObj = uriObj.toURL();
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.err.println("HTTP错误代码: " + responseCode + " - " + url);
                return null;
            }
            
            // 获取响应内容
            StringBuilder content = new StringBuilder();
            try (InputStream is = connection.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            
            return content.toString();
        } catch (Exception e) {
            System.err.println("获取页面内容失败: " + url + " - " + e.getMessage());
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    /**
     * 处理页面内容
     * 
     * @param url 页面URL
     * @param content 页面内容
     */
    private void processContent(String url, String content) {
        // 清理HTML标签
        String text = removeHtmlTags(content);
        
        // 存储爬取的数据
        synchronized (crawledData) {
            crawledData.add(text);
        }
        
        System.out.println("已爬取: " + url + " (长度: " + text.length() + " 字符)");
    }
    
    /**
     * 移除HTML标签
     * 
     * @param html HTML内容
     * @return 纯文本内容
     */
    private String removeHtmlTags(String html) {
        // 使用正则表达式移除HTML标签
        String noTags = html.replaceAll("<[^>]*>", "");
        // 替换HTML实体
        String noEntities = noTags.replaceAll("&[^;]+;", " ");
        // 移除多余空白
        return noEntities.replaceAll("\\s+", " ").trim();
    }
    /**
     * 从页面中提取URL
     * 
     * @param baseUrl 基础URL
     * @param content 页面内容
     * @return URL列表
     */
    private List<String> extractUrls(String baseUrl, String content) {
        List<String> urls = new ArrayList<>();
        
        try {
            // 使用正则表达式匹配所有<a>标签中的href属性
            Pattern pattern = Pattern.compile("<a\\s+(?:[^>]*?\\s+)?href=[\"']([^\"']*)[\"']", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(content);
            
            while (matcher.find()) {
                String href = matcher.group(1);
                String newUrl = resolveUrl(baseUrl, href);
                if (isValidUrl(newUrl)) {
                    urls.add(newUrl);
                }
            }
        } catch (Exception e) {
            System.err.println("提取URL失败: " + e.getMessage());
        }
        
        return urls;
    }
    
    /**
     * 解析相对URL为绝对URL
     * 
     * @param baseUrl 基础URL
     * @param relativeUrl 相对URL
     * @return 绝对URL
     */
    private String resolveUrl(String baseUrl, String relativeUrl) {
        try {
            // 如果已经是绝对URL，直接返回
            if (relativeUrl.startsWith("http://") || relativeUrl.startsWith("https://")) {
                return relativeUrl;
            }
            
            java.net.URI baseUri = new java.net.URI(baseUrl);
            URL base = baseUri.toURL();
            // 处理以/开头的相对路径
            if (relativeUrl.startsWith("/")) {
                URI resolvedUri = baseUri.resolve(relativeUrl);
                return resolvedUri.toURL().toString();
            }
            // 处理../和./的相对路径
            String path = base.getPath();
            if (path.endsWith("/")) {
                URI resolvedUri = baseUri.resolve(path + relativeUrl);
                return resolvedUri.toURL().toString();
            } else {
                // 移除最后一个路径段
                int lastSlash = path.lastIndexOf('/');
                if (lastSlash >= 0) {
                    path = path.substring(0, lastSlash + 1);
                } else {
                    path = "/";
                }
                URI resolvedUri = baseUri.resolve(path + relativeUrl);
                return resolvedUri.toURL().toString();
            }
        } catch (Exception e) {
            return "";
        }
    }
    /**
     * 验证URL是否有效
     * 
     * @param url 要验证的URL
     * @return 是否有效
     */
    private boolean isValidUrl(String url) {
        // 排除非HTTP/HTTPS链接
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return false;
        }
        
        // 排除常见的非文本内容
        String lowerUrl = url.toLowerCase();
        String[] excludeExtensions = {".jpg", ".jpeg", ".png", ".gif", ".pdf", ".zip", ".rar", ".exe", ".mp3", ".mp4", ".avi"};
        for (String ext : excludeExtensions) {
            if (lowerUrl.endsWith(ext)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 获取爬取的数据
     * 
     * @return 爬取的数据列表
     */
    public List<String> getCrawledData() {
        return new ArrayList<>(crawledData);
    }
    
    /**
     * 保存爬取的数据到文件
     * 
     * @param filePath 文件路径
     * @throws IOException 如果保存失败
     */
    public void saveDataToFile(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String data : crawledData) {
                writer.write(data);
                writer.newLine();
                writer.newLine();
            }
        }
    }
    
    /**
     * 清除爬虫状态，准备新的爬取任务
     */
    public void reset() {
        visitedUrls.clear();
        urlQueue.clear();
        crawledData.clear();
    }
    
}
