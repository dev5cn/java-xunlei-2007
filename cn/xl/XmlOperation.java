package cn.xl;

import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * 对保存了下载任务信息的xml文档进行读写操作.
 */

public class XmlOperation
{
	
	/**
	 * 单纯的返回一个文档树的根
	 * @return
	 */
	private static Document getDocument()
	{

		DocumentBuilderFactory DB=DocumentBuilderFactory.newInstance();
		try
		{//通过工厂得到DocumentBuilder实例..
			DocumentBuilder db=DB.newDocumentBuilder();
			//得到一个文档树的根.
			Document doc=db.newDocument();
			return doc;
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
			return null;
		}
	}



	/**
	 * 将任务信息写入task.xml文档.出现以下几种情况时调用此方法.
	 * 		1.有任务完成时调用.
	 * 		2.有任务中断时调用.
	 * 		3.有新任务时调用.
	 * 		4.删除一个任务时调用.
	 * 		5.顶层容器被关闭时调用.
	 * @throws Exception
	 */
	public void writerTaskInfo() throws Exception
	{
		//得到文档树.
		Document document=XmlOperation.getDocument();

		//根节点.根节点用于添加子节点.
		Element xunlei=document.createElement("xunlei");
		//文档树添加根节点.
		document.appendChild(xunlei);

		/*通过集合类中的下载线程锁,去取出相应的下载信息.如果不方便.直接从表格取.
		 * */
		List<Object> list=SaveRunTime.getLIST();
		Iterator it=list.iterator();//得到一个迭代器.
		int i=0;
		while(it.hasNext())
		{	
			DownLoad downLoad=(DownLoad)it.next();
			
			int downLoadSign=downLoad.getDownLoadSign();//状态.
			
			//如果任务不处于中断状态,则全部为暂停.与正在运行时的线程暂停区分开.
			if(downLoadSign!=-1)downLoadSign=-3;
			
			String fileName=downLoad.getDownLoadFileName();//文件名.
			String directory=downLoad.getDownLoadFileDirectory();//文件路径.
			
			String schedule=downLoad.new CounterThread().getProgress(new XDownTable().TestFile(directory+fileName));//进度.
			
			//String speed="--"; String remainTime="--";
			//速度与剩余时间.此两项的值是确定的.
			
			String time=downLoad.second+"";//已用时间
																		//i在这里用到.		
			String fileType=(String)SaveRunTime.getDOWN_TABLE().getValueAt(i, 6);//文件类型,直接从表格获取.
			if("".equals(fileType)||fileType==null)
			{
				fileType="未知";
			}
			
			String url=downLoad.getURL(downLoad);//链接地址. //文件长度.
			String fileLength=downLoad.new CounterThread().getProgress(downLoad.getFileLength(downLoad))+"";
			
			String host=downLoad.getHost(downLoad);//主机名.
			if("".equals(host)||host==null)
			{
				host="未知";
			}
			
			
			
			//单个任务.节点<task></task>
			Element e_Task=document.createElement("task");

			Element e_Condition=document.createElement("condition");//状态.
			e_Condition.appendChild(document.createTextNode(downLoadSign+""));

			Element e_FileName=document.createElement("fileName");//文件名.
			e_FileName.appendChild(document.createTextNode(fileName));

			Element e_Schedule=document.createElement("schedule");//进度.
			e_Schedule.appendChild(document.createTextNode(schedule));

			Element e_Speed=document.createElement("speed");//速度.
			e_Speed.appendChild(document.createTextNode("--"));

			Element e_RemainTime=document.createElement("remainTime");//剩余时间
			e_RemainTime.appendChild(document.createTextNode("--"));

			Element e_Time=document.createElement("time");//已用时间
			e_Time.appendChild(document.createTextNode(time));

			Element e_FileType=document.createElement("fileType");//文件类型.
			e_FileType.appendChild(document.createTextNode(fileType));

			Element e_Directory=document.createElement("directory");//保存路径.
			e_Directory.appendChild(document.createTextNode(directory));

			Element e_Url=document.createElement("url");//链接地址.
			e_Url.appendChild(document.createTextNode(url));


			Element e_FileLength=document.createElement("fileLength");//文件长度.
			e_FileLength.appendChild(document.createTextNode(fileLength));

			Element e_Host=document.createElement("host");
			e_Host.appendChild(document.createTextNode(host));//主机名.

			e_Task.appendChild(e_Condition);
			e_Task.appendChild(e_FileName);
			e_Task.appendChild(e_Schedule);
			e_Task.appendChild(e_Speed);
			e_Task.appendChild(e_RemainTime);
			e_Task.appendChild(e_Time);
			e_Task.appendChild(e_FileType);
			e_Task.appendChild(e_Directory);		//添加.
			e_Task.appendChild(e_Url);
			e_Task.appendChild(e_FileLength);
			e_Task.appendChild(e_Host);

			
			//根节点添加task
			xunlei.appendChild(e_Task);
			i++;
		}


		//解析工厂.
		TransformerFactory tff=TransformerFactory.newInstance();
		
		Transformer tf=tff.newTransformer();//变压器?
		
		tf.setOutputProperty(OutputKeys.ENCODING, "gb2312");//指定字符集.
		tf.setOutputProperty(OutputKeys.INDENT, "yes");//换行?
		
		File file=new File(new File("").getAbsolutePath()+"/xml/task.xml");
		
		PrintWriter pw=new PrintWriter(file);//输入流.
		StreamResult sr=new StreamResult(pw);//结果树.

		
		tf.transform(new DOMSource(document),sr);//写.
		pw.close();//关闭流.
	}



	
	/**
	 * 用于读于task.xml文件中保存的任务信息
	 * 程序最早运行时调用此方法加载任务信息.
	 */
	public Vector<Vector> readTaskInfo() throws Exception
	{	
		//从工厂得实例.
		DocumentBuilder DB=DocumentBuilderFactory.newInstance().newDocumentBuilder();

		Document doc=DB.parse(new File("").getAbsolutePath()+"/xml/task.xml");//文档树的根.
		
		Element root=doc.getDocumentElement();//得到根节点.<xunlei>
		NodeList task=root.getElementsByTagName("task");//得到任务节点.

		//集合类,存放任务信息.
		Vector<Vector> VC=new Vector<Vector>();

		for(int j=0;j<task.getLength();j++)
		{
			Node task1=(Node) task.item(j);//得到第j个任务节点.
			NodeList taskChild=task1.getChildNodes();//得到此节点的子节点.
			Vector<Object>  vc=new Vector<Object>();
			
			for(int i=0;i<taskChild.getLength();i++)
			{
				try
				{	//得到有效值.
					vc.add(taskChild.item(i).getFirstChild().getNodeValue());
				}catch (NullPointerException e)
				{
					continue;
				}
			}
			//如果有历史任务存在,值的数目是确定的.共11个值,否则VC.size()=0
			vc.trimToSize();
			VC.add(vc);
		}
		return VC;
	}

}






















