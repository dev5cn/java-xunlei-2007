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
 * �Ա���������������Ϣ��xml�ĵ����ж�д����.
 */

public class XmlOperation
{
	
	/**
	 * �����ķ���һ���ĵ����ĸ�
	 * @return
	 */
	private static Document getDocument()
	{

		DocumentBuilderFactory DB=DocumentBuilderFactory.newInstance();
		try
		{//ͨ�������õ�DocumentBuilderʵ��..
			DocumentBuilder db=DB.newDocumentBuilder();
			//�õ�һ���ĵ����ĸ�.
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
	 * ��������Ϣд��task.xml�ĵ�.�������¼������ʱ���ô˷���.
	 * 		1.���������ʱ����.
	 * 		2.�������ж�ʱ����.
	 * 		3.��������ʱ����.
	 * 		4.ɾ��һ������ʱ����.
	 * 		5.�����������ر�ʱ����.
	 * @throws Exception
	 */
	public void writerTaskInfo() throws Exception
	{
		//�õ��ĵ���.
		Document document=XmlOperation.getDocument();

		//���ڵ�.���ڵ���������ӽڵ�.
		Element xunlei=document.createElement("xunlei");
		//�ĵ�����Ӹ��ڵ�.
		document.appendChild(xunlei);

		/*ͨ���������е������߳���,ȥȡ����Ӧ��������Ϣ.���������.ֱ�Ӵӱ��ȡ.
		 * */
		List<Object> list=SaveRunTime.getLIST();
		Iterator it=list.iterator();//�õ�һ��������.
		int i=0;
		while(it.hasNext())
		{	
			DownLoad downLoad=(DownLoad)it.next();
			
			int downLoadSign=downLoad.getDownLoadSign();//״̬.
			
			//������񲻴����ж�״̬,��ȫ��Ϊ��ͣ.����������ʱ���߳���ͣ���ֿ�.
			if(downLoadSign!=-1)downLoadSign=-3;
			
			String fileName=downLoad.getDownLoadFileName();//�ļ���.
			String directory=downLoad.getDownLoadFileDirectory();//�ļ�·��.
			
			String schedule=downLoad.new CounterThread().getProgress(new XDownTable().TestFile(directory+fileName));//����.
			
			//String speed="--"; String remainTime="--";
			//�ٶ���ʣ��ʱ��.�������ֵ��ȷ����.
			
			String time=downLoad.second+"";//����ʱ��
																		//i�������õ�.		
			String fileType=(String)SaveRunTime.getDOWN_TABLE().getValueAt(i, 6);//�ļ�����,ֱ�Ӵӱ���ȡ.
			if("".equals(fileType)||fileType==null)
			{
				fileType="δ֪";
			}
			
			String url=downLoad.getURL(downLoad);//���ӵ�ַ. //�ļ�����.
			String fileLength=downLoad.new CounterThread().getProgress(downLoad.getFileLength(downLoad))+"";
			
			String host=downLoad.getHost(downLoad);//������.
			if("".equals(host)||host==null)
			{
				host="δ֪";
			}
			
			
			
			//��������.�ڵ�<task></task>
			Element e_Task=document.createElement("task");

			Element e_Condition=document.createElement("condition");//״̬.
			e_Condition.appendChild(document.createTextNode(downLoadSign+""));

			Element e_FileName=document.createElement("fileName");//�ļ���.
			e_FileName.appendChild(document.createTextNode(fileName));

			Element e_Schedule=document.createElement("schedule");//����.
			e_Schedule.appendChild(document.createTextNode(schedule));

			Element e_Speed=document.createElement("speed");//�ٶ�.
			e_Speed.appendChild(document.createTextNode("--"));

			Element e_RemainTime=document.createElement("remainTime");//ʣ��ʱ��
			e_RemainTime.appendChild(document.createTextNode("--"));

			Element e_Time=document.createElement("time");//����ʱ��
			e_Time.appendChild(document.createTextNode(time));

			Element e_FileType=document.createElement("fileType");//�ļ�����.
			e_FileType.appendChild(document.createTextNode(fileType));

			Element e_Directory=document.createElement("directory");//����·��.
			e_Directory.appendChild(document.createTextNode(directory));

			Element e_Url=document.createElement("url");//���ӵ�ַ.
			e_Url.appendChild(document.createTextNode(url));


			Element e_FileLength=document.createElement("fileLength");//�ļ�����.
			e_FileLength.appendChild(document.createTextNode(fileLength));

			Element e_Host=document.createElement("host");
			e_Host.appendChild(document.createTextNode(host));//������.

			e_Task.appendChild(e_Condition);
			e_Task.appendChild(e_FileName);
			e_Task.appendChild(e_Schedule);
			e_Task.appendChild(e_Speed);
			e_Task.appendChild(e_RemainTime);
			e_Task.appendChild(e_Time);
			e_Task.appendChild(e_FileType);
			e_Task.appendChild(e_Directory);		//���.
			e_Task.appendChild(e_Url);
			e_Task.appendChild(e_FileLength);
			e_Task.appendChild(e_Host);

			
			//���ڵ����task
			xunlei.appendChild(e_Task);
			i++;
		}


		//��������.
		TransformerFactory tff=TransformerFactory.newInstance();
		
		Transformer tf=tff.newTransformer();//��ѹ��?
		
		tf.setOutputProperty(OutputKeys.ENCODING, "gb2312");//ָ���ַ���.
		tf.setOutputProperty(OutputKeys.INDENT, "yes");//����?
		
		File file=new File(new File("").getAbsolutePath()+"/xml/task.xml");
		
		PrintWriter pw=new PrintWriter(file);//������.
		StreamResult sr=new StreamResult(pw);//�����.

		
		tf.transform(new DOMSource(document),sr);//д.
		pw.close();//�ر���.
	}



	
	/**
	 * ���ڶ���task.xml�ļ��б����������Ϣ
	 * ������������ʱ���ô˷�������������Ϣ.
	 */
	public Vector<Vector> readTaskInfo() throws Exception
	{	
		//�ӹ�����ʵ��.
		DocumentBuilder DB=DocumentBuilderFactory.newInstance().newDocumentBuilder();

		Document doc=DB.parse(new File("").getAbsolutePath()+"/xml/task.xml");//�ĵ����ĸ�.
		
		Element root=doc.getDocumentElement();//�õ����ڵ�.<xunlei>
		NodeList task=root.getElementsByTagName("task");//�õ�����ڵ�.

		//������,���������Ϣ.
		Vector<Vector> VC=new Vector<Vector>();

		for(int j=0;j<task.getLength();j++)
		{
			Node task1=(Node) task.item(j);//�õ���j������ڵ�.
			NodeList taskChild=task1.getChildNodes();//�õ��˽ڵ���ӽڵ�.
			Vector<Object>  vc=new Vector<Object>();
			
			for(int i=0;i<taskChild.getLength();i++)
			{
				try
				{	//�õ���Чֵ.
					vc.add(taskChild.item(i).getFirstChild().getNodeValue());
				}catch (NullPointerException e)
				{
					continue;
				}
			}
			//�������ʷ�������,ֵ����Ŀ��ȷ����.��11��ֵ,����VC.size()=0
			vc.trimToSize();
			VC.add(vc);
		}
		return VC;
	}

}






















