package org.mongo.config;


import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.bg.BulgarianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.mongo.domain.Campaign;
import org.mongo.index.IndexComponent;
import org.mongo.scheduler.SchedulerService;
import org.mongo.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.mongodb.MongoException;
import com.mongodb.MongoURI;

@Configuration
@PropertySource({"classpath:social.properties"})
@ComponentScan(basePackages={"org.mongo.service","org.mongo.domain"})
@EnableMongoRepositories("org.mongo.repository")
@EnableScheduling
public class RootConfig {
	 
	@Autowired
	Environment env;
	
	@Autowired
	ServletContext context;

	@Bean
	public Version luceneVersion(){
		return Version.LUCENE_46;
	}
	@Bean
	public Analyzer  analyzer(){
		return new BulgarianAnalyzer(luceneVersion());
	}
	
	@Bean
	public Directory luceneDirectory() throws IOException{
		return FSDirectory.open(new File(context.getRealPath("/tmp/index")));
	}
	
//	@Bean
//	public Directory luceneDirectory() throws IOException{
//		return FSDirectory.open(new File("/tmp/index"));
//	}
	
	private void initWriter() throws CorruptIndexException, LockObtainFailedException, IOException{
		IndexWriter writer = indexWriter();
		writer.deleteAll();
//		List<Campaign> campaigns = CommonUtils.parseAllCampaigns();
		List<Campaign> campaigns =   mongoTemplate().find(new Query(), Campaign.class);
		for (Campaign campaign : campaigns) {
			Document doc = new Document();
			doc.add(new TextField("description", CommonUtils.normalizeSpaces( campaign.getDescription() ),Store.NO));
			doc.add(new LongField("campaignId",campaign.getCampaignId(), Store.YES));
			doc.add(new TextField("title", campaign.getTitle(), Store.NO));
			doc.add(new TextField("longDescription",CommonUtils.normalizeSpaces(campaign.getLongDescription()),Store.NO));
	        doc.add(new IntField("type", campaign.getType(),Field.Store.YES));
	        writer.addDocument(doc);
		}
		writer.commit();
		writer.close();
	}
	
	@Bean
	public IndexWriter indexWriter() throws CorruptIndexException, LockObtainFailedException, IOException{
		IndexWriter iwriter = new IndexWriter(luceneDirectory(),config());
		return iwriter;
	}
	
	@Bean
	public IndexWriterConfig config(){
		IndexWriterConfig conf = new IndexWriterConfig(luceneVersion(), analyzer());
		return conf;
	}

	@Bean
	public MongoDbFactory mongoDbFactory() throws MongoException, UnknownHostException {
		MongoDbFactory factory = new SimpleMongoDbFactory(new MongoURI(env.getProperty("mongodburl")));
		return factory;
	}
	
	@Bean
	@DependsOn("indexWriter")
	public IndexReader reader() throws CorruptIndexException, IOException{
		initWriter();
		IndexReader reader = DirectoryReader.open(luceneDirectory());
		return reader;
	}
	
	@Bean
	public IndexSearcher searcher() throws CorruptIndexException, IOException{
		IndexSearcher searcher = new IndexSearcher(reader());
		return searcher;
	}
	
	public @Bean SchedulerService service() {
		return new SchedulerService();
	}
	
	@Bean
	@DependsOn(value={"indexWriter","searcher","reader"})
	public IndexComponent indexComponent() throws IOException{
		IndexComponent component = new IndexComponent();
		return component;
	}
	
	@Bean
	public MongoOperations mongoTemplate() throws MongoException, UnknownHostException {
		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
		return mongoTemplate;
	}
	
	
}
