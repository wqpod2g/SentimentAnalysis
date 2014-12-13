package nju.iip;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


/**
 * @since 2014-12-9
 * @author wangqiang
 *
 */
public class Bigram {
	
	private static String train_data_path="data\\train2";//训练文本路径
	
	private static String test_data_path="data\\test2";//测试文本路径
	
	private static String result_file_path="data\\test2.rlabelclass";//测试结果文件存放位置
	
	
	/**
	 * 获得训练样本集
	 * @return
	 */
	public static Map<String,ArrayList<Post>>getTrainPostList(){
		Map<String,ArrayList<Post>>train_post_list=new HashMap<String,ArrayList<Post>>();
		ArrayList<Post>positive_post=new ArrayList<Post>();
		ArrayList<Post>negative_post=new ArrayList<Post>();
		train_post_list.put("positive", positive_post);
		train_post_list.put("negative", negative_post);
		File f=new File(train_data_path);
		String[] file_list=f.list();
		for(String txt_name:file_list){
			String txt_path=train_data_path+File.separator+txt_name;
			String content=Tools.readFile(txt_path);
			String classification=Tools.classifyPost(txt_name);
			Post post=new Post(classification,Tools.segStr(content));
			train_post_list.get(classification).add(post);
		}
		return train_post_list;
	}
	
	/**
	 * 获得测试样本集
	 * @return
	 */
	public static ArrayList<Post>getTestPostList(){
		ArrayList<Post>test_sample=new ArrayList<Post>();
		File f=new File(test_data_path);
		String[] file_list=f.list();
		for(String txt_name:file_list){
			String txt_path=test_data_path+File.separator+txt_name;
			String content=Tools.readFile(txt_path);
			String classification=Tools.classifyPost(txt_name);
			Post post=new Post(classification,Tools.segStr(content),txt_name);
			test_sample.add(post);
		}
		return test_sample;
	}
	
	
	
	/**
	 * @description 创建某类的词典
	 * @param sample
	 * @return
	 */
	public static Map<String,Integer>creatDictionaryMap(ArrayList<Post>sample){
		Map<String,Integer>dictionary_map=new LinkedHashMap<String,Integer>();
		for(int i=0;i<sample.size();i++){
			Post post=sample.get(i);
			ArrayList<String>word_sequence=post.get_words_sequence();
			for(int j=0;j<word_sequence.size();j++){
				if(!dictionary_map.containsKey(word_sequence.get(j))){
					dictionary_map.put(word_sequence.get(j), 1);
				}
				else{
					dictionary_map.put(word_sequence.get(j),dictionary_map.get(word_sequence.get(j))+1);
				}
			}
		}
		return dictionary_map;
	}
		
	
	/**
	 * @description 计算某一类帖子对应的bigram矩阵
	 * @param sample
	 * @return BigramMatrix
	 */
	public static BigramMatrix creatMatrix(ArrayList<Post>sample){
		Map<String,Integer>dictionary_map=creatDictionaryMap(sample);
		HashMap<String,Integer>bigram_map=new HashMap<String,Integer>();
		for(int i=0;i<sample.size();i++){
			Post post=sample.get(i);
			ArrayList<String>word_sequence=post.get_words_sequence();
			for(int j=0;j<word_sequence.size()-1;j++){
				String first_word=word_sequence.get(j);
				String second_word=word_sequence.get(j+1);
				
				String two_continuous_word=first_word+second_word;
				
				if(!bigram_map.containsKey(two_continuous_word)){
					bigram_map.put(two_continuous_word, 1);
				}
				
				else{
					bigram_map.put(two_continuous_word, bigram_map.get(two_continuous_word)+1);
				}
			}
		}
		BigramMatrix bigramMatrix=new BigramMatrix(bigram_map,dictionary_map);
		Set<String>words=dictionary_map.keySet();
		int num=0;
		for(String word:words){
			num=num+dictionary_map.get(word);
		}
		bigramMatrix.setWordsNum(num);
		return bigramMatrix;
	}
	
	
	/**
	 * @description 求得十个bigram矩阵
	 * @param train_sample
	 * @return
	 */
	public static Map<String,BigramMatrix>getMatrixMap(Map<String,ArrayList<Post>>train_sample){
		Map<String,BigramMatrix>ten_matrix_map=new HashMap<String,BigramMatrix>();
		Set<String>txt_names=train_sample.keySet();
		for(String txt_name:txt_names){
			ten_matrix_map.put(txt_name, creatMatrix(train_sample.get(txt_name)));
		}
		return ten_matrix_map;
	}
	
	/**
	 * @description 计算某篇帖子属于某个类的概率
	 * @param post
	 * @param matrix
	 * @return probility
	 */
	public static double getProbility(Post post,BigramMatrix bigramMatrix){
		double probility=0.0;
		HashMap<String,Integer>bigram_map=bigramMatrix.getBigramMap();
		Map<String,Integer>dictionary_map=bigramMatrix.getDictionary_map();
		ArrayList<String>words_sequence=post.get_words_sequence();
		for(int i=0;i<words_sequence.size()-1;i++){
			if(dictionary_map.containsKey(words_sequence.get(i))&&dictionary_map.containsKey(words_sequence.get(i+1))){
				String first_word=words_sequence.get(i);
				String second_word=words_sequence.get(i+1);
				String two_continuous_word=first_word+second_word;
				int two_continuous_word_tf=0;
				if(bigram_map.containsKey(two_continuous_word)){
					two_continuous_word_tf=bigram_map.get(two_continuous_word);
				}
				
				probility=probility+Math.log((two_continuous_word_tf+0.000001)/dictionary_map.get(words_sequence.get(i)));
			}
			
			else{
				probility=probility+Math.log(0.000001/bigramMatrix.getWordNum());
			}
		}
		return probility;
		
	}
	
	/**
	 * @计算某篇帖子属于哪个类
	 * @param post
	 * @param ten_matrix_map
	 * @return 帖子所属类别
	 */
	public static String getResult(Post post,Map<String,BigramMatrix>ten_matrix_map){
		String result="";
		double probility=-Double.POSITIVE_INFINITY;
		Set<String>txt_names=ten_matrix_map.keySet();
		for(String txt_name:txt_names){
			double temp=getProbility(post,ten_matrix_map.get(txt_name));
			if(temp>probility){
				probility=temp;
				result=txt_name;
			}
		}
		return result;
	}
	
	/**
	 * 将测试结果写入文件
	 */
	public static void getResultFile(){
		try {
			FileWriter fw=new FileWriter(result_file_path);
			ArrayList<Post>test_sample=getTestPostList();
			Map<String,ArrayList<Post>>train_sample=getTrainPostList();
			Map<String,BigramMatrix>matrix_map=getMatrixMap(train_sample);
			double count=0;
			for(int i=0;i<test_sample.size();i++){
				String classification="";
				String result=getResult(test_sample.get(i),matrix_map);
				String id=test_sample.get(i).get_post_id();
				if(result.equals(id)){
					count++;
				}
				String txt_name=test_sample.get(i).get_txt_name();
				if(result.equals("positive")){
					classification="+1";
				}
				else{
					classification="-1";
				}
				
				fw.write(txt_name+" "+classification+"\n");
			}
			System.out.println("accuracy is:"+count/test_sample.size());
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		long start=System.currentTimeMillis();
		getTrainPostList();
		getResultFile();
		long end=System.currentTimeMillis();
		System.out.println("程序运行时间为:"+(end-start)/1000+"s");
		System.out.println("test2.rlabelclass存放路径为:"+result_file_path);
	}

}
