package com.itheima.test;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述
 *
 * @author ljh
 * @version 1.0
 * @package com.itheima.test *
 * @Date 2019-12-23
 * @since 1.0
 */
public class ESTest {

    //创建文档  先创建索引 --创建类型 ----再创建文档 (文档需要有一个唯一的标识)
    @Test
    public void createDoc() throws Exception {
        //3.执行创建动作
        //参数1 指定的就是索引名称
        //参数2 指定的类型名称
        //参数3 指定的文档的唯一标识
        /*
        * {
        *   id:1,
        *   title:"好wenzhang",
        *   "content":"就是一片好玩增"
        * }
        *
        * */

        for (int i = 0; i < 100; i++) {
            //一个文档就是一行数据 一行数据就是一个JSON---Map
            Map<String, Object> documentmap = new HashMap<String, Object>();
            documentmap.put("id", i);
            documentmap.put("title", "elasticSearch是一个基于Lucene的搜索服务器"+i);
            documentmap.put("content", "elasticSearch是一个基于Lucene的搜索服务器,很好用,特别好用"+i);

            IndexResponse response = transportClient.prepareIndex("blog", "article", i+"").setSource(documentmap).get();
            System.out.println("版本:" + response.getVersion());
            System.out.println("索引名:" + response.getIndex());
            System.out.println("类型名:" + response.getType());
        }


    }

    TransportClient transportClient;

    @Before
    public void setUp() throws Exception {
        Settings settings = Settings.EMPTY;//不需要配置 用默认的配置项,代表是单机版
        transportClient = new PreBuiltTransportClient(settings);
        //2.添加链接服务器地址
        //1. ip地址
        //2  port 9300 Tcp端口
        transportClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
    }

    @After
    public void close() {
        transportClient.close();
    }

    //从ES中查询所有数据
    @Test
    public void matchAllQuery() throws Exception {


        //3. 创建一个查询对象,设置查询条件,执行查询
        SearchResponse response = transportClient.prepareSearch("blog")
                .setTypes("article")
                .setQuery(QueryBuilders.matchAllQuery()).get();
        //4.获取查询到的结果
        SearchHits hits = response.getHits();
        System.out.println("根据条件查询到的总命中数:" + hits.totalHits);

        for (SearchHit hit : hits) {//一个hit 就是一个document
            Map<String, Object> source = hit.getSource();//一行数据 就是一个JSON--- MAP
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);
            System.out.println("-=======================");
            System.out.println(source);
        }

    }

    //字符查询 先分词再搜索
    @Test
    public void stringquery() {
        //3. 创建一个查询对象,设置查询条件,执行查询
        SearchResponse response = transportClient.prepareSearch("blog")
                .setTypes("article")
                //指定搜索的文本,如果没有指定具体的field 默认会从所有的字段中进行搜索(排除掉非STRING的)
                .setQuery(QueryBuilders.queryStringQuery("很").field("content")).get();
        //4.获取查询到的结果
        SearchHits hits = response.getHits();
        System.out.println("根据条件查询到的总命中数:" + hits.totalHits);

        for (SearchHit hit : hits) {//一个hit 就是一个document
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);
        }
    }


    //词条查询 :不能拆分,要作为整体进行匹配查询
    @Test
    public void termQuery() {
        //3. 创建一个查询对象,设置查询条件,执行查询
        SearchResponse response = transportClient.prepareSearch("blog")
                .setTypes("article")
                // content:很好用
                .setQuery(QueryBuilders.termQuery("content","很")).get();
        //4.获取查询到的结果
        SearchHits hits = response.getHits();
        System.out.println("根据条件查询到的总命中数:" + hits.totalHits);

        for (SearchHit hit : hits) {//一个hit 就是一个document
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);
        }
    }

    //通配符查询 或者叫做模糊查询

    // * 代表任意字符 不占用字符空间
    // ? 代表任意字符 占位符 代表一个字符(任意) 占用一个字符空间
    @Test
    public void wildcardQuery() {
        //3. 创建一个查询对象,设置查询条件,执行查询
        SearchResponse response = transportClient.prepareSearch("blog")
                .setTypes("article")
                // content:很好用
                .setQuery(QueryBuilders.wildcardQuery("content","很*")).get();
        //4.获取查询到的结果
        SearchHits hits = response.getHits();
        System.out.println("根据条件查询到的总命中数:" + hits.totalHits);

        for (SearchHit hit : hits) {//一个hit 就是一个document
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);
        }
    }

}
