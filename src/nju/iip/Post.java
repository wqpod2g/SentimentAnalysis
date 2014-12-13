package nju.iip;

import java.util.ArrayList;


/**
 * @description 帖子类
 * @author wangqiang
 * @since 2014-12-9
 */
public class Post {
	/**
	 * 帖子对应的词序列
	 */
	private ArrayList<String>words_sequence;
	
	/**
	 * 帖子所属类别
	 */
	private String post_id;
	
	
	/**
	 * 帖子名字
	 */
	private String txt_name;
	
	/**
	 * @description 构造函数
	 * @param id
	 * @param word
	 */
	public Post(String id,ArrayList<String> word, String txt_name){
		this.post_id=id;
		this.words_sequence=word;
		this.txt_name=txt_name;
	}
	
	/**
	 * @description 构造函数
	 * @param id
	 * @param word
	 */
	public Post(String id,ArrayList<String> word){
		this.post_id=id;
		this.words_sequence=word;
	}
	
	/**
	 * @decription 返回帖子词序列
	 * @return
	 */
	public ArrayList<String>get_words_sequence(){
		return this.words_sequence;
	}
	
	
	/**
	 * @decription 返回帖子所属类别
	 * @return
	 */
	public String get_post_id(){
		return this.post_id;
	}
	
	/**
	 * @decription 返回帖子名字
	 * @return
	 */
	public String get_txt_name(){
		return this.txt_name;
	}
	

}
