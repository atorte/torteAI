//这个是知识图谱服务类，负责处理知识图谱的逻辑

/*
 * @author torteyun
 * @version 1.0
 * @since 2025-05-01
 */

package com.torteyun.ai.knowledege;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * 知识图谱服务类
 * 负责管理和操作知识图谱，提供知识存储、查询和推理功能
 */
public class KnowledgeGraphService {
    
    // 知识图谱存储结构
    private Map<String, Entity> entities;
    private List<Relation> relations;
    private Map<String, List<Fact>> factsByEntity;
    
    /**
     * 默认构造函数
     */
    public KnowledgeGraphService() {
        this.entities = new HashMap<>();
        this.relations = new ArrayList<>();
        this.factsByEntity = new HashMap<>();
    }
    
    /**
     * 添加实体到知识图谱
     * @param entityId 实体ID
     * @param entityType 实体类型
     * @param properties 实体属性
     * @return 添加的实体对象
     */
    public Entity addEntity(String entityId, String entityType, Map<String, Object> properties) {
        Entity entity = new Entity(entityId, entityType, properties);
        entities.put(entityId, entity);
        factsByEntity.put(entityId, new ArrayList<>());
        return entity;
    }
    
    /**
     * 添加关系到知识图谱
     * @param sourceId 源实体ID
     * @param targetId 目标实体ID
     * @param relationType 关系类型
     * @param properties 关系属性
     * @return 添加的关系对象
     */
    public Relation addRelation(String sourceId, String targetId, String relationType, Map<String, Object> properties) {
        if (!entities.containsKey(sourceId) || !entities.containsKey(targetId)) {
            throw new IllegalArgumentException("源实体或目标实体不存在");
        }
        
        Relation relation = new Relation(entities.get(sourceId), entities.get(targetId), relationType, properties);
        relations.add(relation);
        
        // 创建事实并添加到索引
        Fact fact = new Fact(entities.get(sourceId), relation, entities.get(targetId));
        factsByEntity.get(sourceId).add(fact);
        factsByEntity.get(targetId).add(fact);
        
        return relation;
    }
    
    /**
     * 查询与特定实体相关的所有事实
     * @param entityId 实体ID
     * @return 相关事实列表
     */
    public List<Fact> queryFactsByEntity(String entityId) {
        if (!factsByEntity.containsKey(entityId)) {
            return new ArrayList<>();
        }
        return factsByEntity.get(entityId);
    }
    
    /**
     * 根据关系类型查询事实
     * @param relationType 关系类型
     * @return 符合条件的事实列表
     */
    public List<Fact> queryFactsByRelation(String relationType) {
        List<Fact> results = new ArrayList<>();
        for (Relation relation : relations) {
            if (relation.getType().equals(relationType)) {
                results.add(new Fact(relation.getSource(), relation, relation.getTarget()));
            }
        }
        return results;
    }
    
    /**
     * 执行简单的推理查询
     * @param startEntityId 起始实体ID
     * @param relationPath 关系路径
     * @return 推理结果实体集合
     */
    public Set<Entity> inferEntities(String startEntityId, List<String> relationPath) {
        Set<Entity> currentEntities = new HashSet<>();
        
        if (!entities.containsKey(startEntityId)) {
            return currentEntities;
        }
        
        currentEntities.add(entities.get(startEntityId));
        
        for (String relationType : relationPath) {
            Set<Entity> nextEntities = new HashSet<>();
            for (Entity entity : currentEntities) {
                List<Fact> facts = factsByEntity.get(entity.getId());
                for (Fact fact : facts) {
                    if (fact.getRelation().getType().equals(relationType)) {
                        if (fact.getSubject().equals(entity)) {
                            nextEntities.add(fact.getObject());
                        }
                    }
                }
            }
            currentEntities = nextEntities;
            if (currentEntities.isEmpty()) {
                break;
            }
        }
        
        return currentEntities;
    }
    
    /**
     * 获取所有实体
     * @return 实体映射
     */
    public Map<String, Entity> getAllEntities() {
        return entities;
    }
    
    /**
     * 获取所有关系
     * @return 关系列表
     */
    public List<Relation> getAllRelations() {
        return relations;
    }
    
    /**
     * 实体类
     */
    public static class Entity {
        private String id;
        private String type;
        private Map<String, Object> properties;
        
        public Entity(String id, String type, Map<String, Object> properties) {
            this.id = id;
            this.type = type;
            this.properties = properties != null ? properties : new HashMap<>();
        }
        
        public String getId() {
            return id;
        }
        
        public String getType() {
            return type;
        }
        
        public Map<String, Object> getProperties() {
            return properties;
        }
        
        public Object getProperty(String key) {
            return properties.get(key);
        }
    }
    
    /**
     * 关系类
     */
    public static class Relation {
        private Entity source;
        private Entity target;
        private String type;
        private Map<String, Object> properties;
        
        public Relation(Entity source, Entity target, String type, Map<String, Object> properties) {
            this.source = source;
            this.target = target;
            this.type = type;
            this.properties = properties != null ? properties : new HashMap<>();
        }
        
        public Entity getSource() {
            return source;
        }
        
        public Entity getTarget() {
            return target;
        }
        
        public String getType() {
            return type;
        }
        
        public Map<String, Object> getProperties() {
            return properties;
        }
    }
    
    /**
     * 事实类 - 表示主语-谓语-宾语三元组
     */
    public static class Fact {
        private Entity subject;
        private Relation relation;
        private Entity object;
        
        public Fact(Entity subject, Relation relation, Entity object) {
            this.subject = subject;
            this.relation = relation;
            this.object = object;
        }
        
        public Entity getSubject() {
            return subject;
        }
        
        public Relation getRelation() {
            return relation;
        }
        
        public Entity getObject() {
            return object;
        }
        
        @Override
        public String toString() {
            return subject.getId() + " -[" + relation.getType() + "]-> " + object.getId();
        }
    }
}



