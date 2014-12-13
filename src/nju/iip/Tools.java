package nju.iip;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class Tools {
	
	 /**
     * 
    * @Title: segStr
    * @Description: 返回帖子的词序列
    * @param @param content
    * @param @return    
    * @return ArrayList<String>   
    * @throws
     */
    public static ArrayList<String> segStr(String content){
        // 分词
    	StringReader input = new StringReader(content);
        IKSegmenter iks = new IKSegmenter(input, true);
        Lexeme lexeme = null;
        ArrayList<String> words = new ArrayList<String>();
        try {
            while ((lexeme = iks.next()) != null) {
            		words.add(lexeme.getLexemeText());
            }       
        }catch(IOException e) {
            e.printStackTrace();
        }
        return words;
    }
    
    
    /**
     * @decription 计算平均值
     * @param list
     * @return
     */
    public static Double getMean(ArrayList<Double>list){
    	Double sum=0.0;
		for(int i=0;i<list.size();i++){
			sum=sum+list.get(i);
		}
		Double mean=sum/10;
		return mean;
    }
    
    /**
     * 读出txt文件内容
     * @param file_path
     * @return
     */
    public static String readFile(String file_path){
    	String content="";
    	try{
    		FileInputStream fs=new FileInputStream(file_path);
    		InputStreamReader is=new InputStreamReader(fs,"GBK");
    		BufferedReader br=new BufferedReader(is);
    		String line=br.readLine();
    		while(line!=null){
    			content=content+line;
    			line=br.readLine();
    		}
    		br.close();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return content;
    }
    
    
    /**
	 * 提取txt文件评分并返回类别
	 * @param txt_name
	 * @return
	 */
	public static String classifyPost(String txt_name){
		String classification="";
		Pattern p=Pattern.compile("\\d\\.\\d");
		Matcher m=p.matcher(txt_name);
		String str="";
		if(m.find()){
			str=m.group();
		}
		double score=Double.parseDouble(str);
		if(score>=4.5){
			classification="positive";
		}
		if(score<=2.0){
			classification="negative";
		}
		return classification;
	}

}
