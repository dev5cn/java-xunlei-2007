package cn.xl;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JTable;


/**
 * �߳���,����ʵ��.
 */
class DownLoad extends Thread
{
	
	private long  readTotal=0;	//�Ѷ����ֽ���.
	private long point=0;//�ϵ�.
	private String fileName="";//�ļ���.
	private long fileLength=0;//�ļ�����.
	private String hostName="";//�Է�������.
	private String fileType="";  //��������Ӧ������Ϊ "",����null,�����׳��쳣.
	private String link="";	//���ӵ�ַ.
	public  int row=0;			//������.
	private String saveFileName="";//���Ϊ�ļ���.
	private String saveDirectory="";//����·��.
	
	
	private URL url=null;//������.
	private URLConnection URLC=null;
	
	//���������.
	private InputStream ins=null;
	private DataInputStream  datainput=null;
	
	
	/**
	 * �����߳̿���.1Ϊ����״̬,0Ϊ�ȴ�,-1Ϊ�ж�(�������жϵ���ʷ����).-2Ϊ�ֶ�ɾ��.-3Ϊ����������ʷ��¼.
	 */
	private int downLoadSign=-1;
	
	/**
	 * ����ɵ�������.ֻ��saveTaskEnd()����ʹ�ô˾�̬����.
	 */
	private static int TASK_COMPLETE=0;
	
	/**
	 * ����ʱ��.���߳���������ֵ.
	 */
	public long second=0;
	
	
	
	
	/**
	 * Ĭ�Ϲ��췽��.
	 */	
	public DownLoad(){}
	
	
	/**
	 * ���췽��,�½���������ʱ����,�������� 1,�к�����.2,����·��,3,�ļ�����·��,4,��������ļ���. 5,�ϵ�.
	 * @param row
	 * @param link
	 * @param saveDirectory
	 * @param saveFileName
	 * @param point
	 */
	DownLoad(int row,String link,String saveDirectory,String saveFileName,long point)
	{
		this.row=row;
		this.link=link;
		this.saveFileName=saveFileName;
		this.saveDirectory=saveDirectory;
		this.point=point;
	}
		
	
	/**
	 *����ʵ��. 
	 */
	public void run()
	{	
		
		DownLoad downLoad=(DownLoad)SaveRunTime.getLIST().get(row);//�����߳���.
		downLoad.downLoadSign=1;//����.�̴߳��ڿ�����״̬.
		CounterThread counter=(CounterThread)SaveRunTime.getTIMELIST().get(row);//��ʱ��.
		JTable DOWNTABLE=SaveRunTime.getDOWN_TABLE();//�����.���Ʊ��仯.
		try
		{
			datainput=new DataInputStream(ins);//������������.
			RandomAccessFile raf=new RandomAccessFile(saveDirectory+saveFileName,"rw");//�����.
			raf.seek(point);//ָ�������ϵ㴦.
			int by=0;
			
			try
			{
				by=datainput.read();//��һ���ֽ�.
			}
			catch (SocketException e)//�����׳��쳣,ֻ���ж��߳�,
			{
				//����������.֪ͨ�߳���Ҫ���ж�.
				System.out.println("����������.");
				downLoad.downLoadSign=-1;//�����߳̽����ж�.
			}
			
			
			System.out.println("�ļ�����:"+fileLength);
			System.out.println("����һ����ʱ�߳�...");
			
			
			CounterThread count_Speed=new CounterThread();
			new Thread(count_Speed).start();//����һ���ֽں�������ʱ�߳�.
			
			SaveRunTime.getSPEED_LIST().add(count_Speed);//�������������еļ�ʱ�̶߳���.
			while(by!=-1)
			{
				if(downLoad.downLoadSign!=1)
				{				
					synchronized(downLoad)
					{		
						if(downLoad.downLoadSign==0)//�߳̽���ȴ�.
						{
							counter.timeSign=0; //֪ͨ��ʱ�߳̽���ȴ�.
							
							count_Speed.timeSign=-1;//֪ͨ��ʱ�ٶȼ�����,�������������ٶ�.
							
							downLoad.wait();//�����߳̽���ȴ�.
							
							synchronized (counter)//����ʱ���Ѽ�ʱ�߳�.
							{
								counter.notify();
							}
							//��������ͼ��.
							DOWNTABLE.setValueAt(new XDownTable().getImageIcon("/image/ing.png"), row, 0);
							downLoad.downLoadSign=1;//���ÿ���.
							
							count_Speed.timeSign=1;
						}
						else if(downLoad.downLoadSign==-1)//�߳���Ҫ���ж�.�����쳣�ж�ʱ����.
						{	
							//�����ж�ͼ��.
							DOWNTABLE.setValueAt(new XDownTable().getImageIcon("/image/die.png"), row, 0);
							
							count_Speed.timeSign=-1;
							
							counter.timeSign=-1;//֪ͨ��ʱ�߳��ж�.
							Thread.currentThread().interrupt();//�����̱߳��ж�.
							System.out.println("�ò�������,�����߳��ж�."+Thread.currentThread().isInterrupted());
							return;
						}
						else if(downLoad.downLoadSign==-2)//�ֶ�ǿ��ɾ������,ֻ�е�����������ʱ�ű�����.
						{
							count_Speed.timeSign=-1;
							
							counter.timeSign=-2; //֪ͨ��ʱ�߳���Ҫ�ж�.
							Thread.currentThread().interrupt();//�����̱߳��ж�.
							return;
						}
					}
				}
				try
				{
					raf.write(by);//дһ���ֽ�.
					by=datainput.read();//��һ���ֽ�,������׳�SocketExcetpion.
					readTotal++;//�ۼƶ������ֽ�,���ڼ�ʱ�̼߳������.
				}
				catch (SocketException e)
				{
					//����������.֪ͨ�߳���Ҫ���ж�.
					System.out.println("����������.");
					downLoad.downLoadSign=-1;//�����߳̽����ж�.
				}
			}
			//forѭ�����.
			datainput.close();
			raf.close();//�ر���.
			
			saveTaskEnd(downLoad);//������ɵ�������Ϣ�� �����ر�.
			count_Speed.timeSign=-1;//֪ͨ��ʱ�ٶȼ�����.
			counter.timeSign=-3;    //֪ͨ��ʱ�߳�,�������.
			Thread.currentThread().interrupt();//�ж��߳�.
			System.out.println("�����߳��ж�:"+Thread.currentThread().isInterrupted());
			System.out.println("�������");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	
	/**
	 *  ������������Ļ�����Ϣ.�õ�����ʱ����true ,�˷����������ֶθ�ֵ.
	 *	����.link,url,URLC,ins,fileName,fileLength,fileType,hostName.
	 *	��������߳�û������,�򲿷��ֶ�ֵΪnull,
	 *	�����ɲ����̵߳���.
	 */
	public boolean fileInfo()
	{
		JTable DOWNINFO_TABLE=SaveRunTime.getDOWN_INFOTABLE();
		DOWNINFO_TABLE.setValueAt("���ڻ�ȡ����......", 0, 0);
		DOWNINFO_TABLE.setValueAt("",1,0);
		DOWNINFO_TABLE.setValueAt("",1,1);
		DOWNINFO_TABLE.setValueAt("",2,0);
		DOWNINFO_TABLE.setValueAt("",2,1);
		DOWNINFO_TABLE.setValueAt("",3,0);
		DOWNINFO_TABLE.setValueAt("",3,1);
		DOWNINFO_TABLE.setValueAt("",4,0);
		DOWNINFO_TABLE.setValueAt("",4,1);
		DOWNINFO_TABLE.setValueAt("",5,0);
		DOWNINFO_TABLE.setValueAt("",5,1);
		DOWNINFO_TABLE.setValueAt("",6,0);
		DOWNINFO_TABLE.setValueAt("",6,1);
		try
		{
			url=new URL(link);
			URLC=url.openConnection();//�õ�����.		//�ϵ�.
			URLC.setRequestProperty("RANGE", "bytes="+point+"-");
			ins=URLC.getInputStream();
		
			//System.out.println(URLC.getHeaderFields());393696992
			
			
			//�ļ���.ֱ��ȡ�����ӵ�ַ.
			fileName=interceptURL(url.getFile());
			DOWNINFO_TABLE.setValueAt("�ļ�����", 1, 0);
			DOWNINFO_TABLE.setValueAt(fileName, 1, 1);
			//�ļ�����.
			fileLength=URLC.getContentLength()+point;
			float f=((float)fileLength)/((float)(1024*1024)+(point/(1024f*1024f)));
			DOWNINFO_TABLE.setValueAt("�ļ�����", 2, 0);
			DOWNINFO_TABLE.setValueAt(new CounterThread().interceptFolat(f)+"M", 2, 1);
			//�ļ�����.
			fileType=URLC.getContentType();
			if(fileType==null||"".equals(fileType))
			{
				fileType="δ֪";
			}
			DOWNINFO_TABLE.setValueAt("�ļ�����", 3, 0);
			DOWNINFO_TABLE.setValueAt(fileType, 3, 1);
			//����·��.
			DOWNINFO_TABLE.setValueAt("����·��", 4, 0);
			DOWNINFO_TABLE.setValueAt(saveDirectory, 4, 1);
			//���ӵ�ַ.
			DOWNINFO_TABLE.setValueAt("���ӵ�ַ", 5, 0);
			DOWNINFO_TABLE.setValueAt(link, 5, 1);
			//�Է�������.
			hostName=url.getHost();
			DOWNINFO_TABLE.setValueAt("�Է�����", 6, 0);
			DOWNINFO_TABLE.setValueAt(hostName, 6, 1);
			
		
			
			
			//û���쳣���ʱ;��Ӽ�ֵ��.
			HashMap<String,String> hm=new HashMap<String,String>(7);
			hm.put("���ڻ�ȡ����...","");
			hm.put("�ļ�����",saveFileName);
			hm.put("�ļ�����",new CounterThread().interceptFolat(f)+"M");
			hm.put("�ļ�����",fileType);
			hm.put("����·��",saveDirectory+saveFileName);
			hm.put("���ӵ�ַ",link);
			hm.put("�Է�����",hostName);
			SaveRunTime.getSAVE_HASHMAP().add(hm);
			return true;
		}
		catch (Exception e)
		{	
			return false;
		}
	}
	
	
	/**
	 * �߳���,�����������ļ�ʱ�ٶ�.�������翪ʼ����ʱ�������߳�.һֱ���������򱻹ر�.
	 */
	class CounterSpeed implements Runnable
	{
		
		public void run()
		{
			/* List�б�����Ǽ�ʱ�̶߳���.���������ڿ��Ƽ�ʱ.�����ڳ�������ʱ��timeSign������ֵ�ĸı�,������
			 * ֪ͨ��ʱ�ٶȼ����������Ƿ������̵߳������ٶ�.
			 * */
			long speed=0;
			for(;;)
			{
				List<Object> counter=SaveRunTime.getSPEED_LIST();//������������ʱ�̺߳󱻱���.
				for(int i=0;i<counter.size();i++)
				{	
					CounterThread count_Speed=(CounterThread)counter.get(i);
					if(count_Speed.timeSign==1)
					{
						speed=speed+count_Speed.count;
					}
				}
				//����Jlabel�ı仯.
				SaveRunTime.getSPEED_JLABEL().setText("\u901f\u5ea6:"+new CounterThread().interceptFolat(speed/(1024f))+"KB/S");
				speed=0;//����.
				try
				{
					Thread.sleep(500);//˯�߰���.
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	/**
	 * �߳���,�������̵߳���.���ڼ��㵥������� ����,�ٶ�,ʣ��ʱ��,����ʱ��.
	 *
	 */
	class CounterThread implements Runnable
	{
		/**
		 *�߳̿��ƿ���.�ĸ���Чֵ:1.����.0,�ȴ�.-1,�ж�.-2,�ֶ�ɾ��.-3,�������.
		 */
		public int timeSign=1;
		
		/**
		 * ÿ��һ��,�����ֵ.�õ������ٶ�.
		 */
		private long count=0; 
		
		/**
		 * ��ʱ����.����һ��֮ǰ�Ķ����ֽ���.
		 */
		private long temp=0;
		
		/**
		 * �������.
		 */
		private String downLoadSchedule=null;
		
		/**
		 * ����ʵ��,�����̶߳�����һ���ֽں�����.
		 */
		public void run()
		{								
			CounterThread inner=(CounterThread)SaveRunTime.getTIMELIST().get(row);//��.
			final JTable DOWNTABLE=SaveRunTime.getDOWN_TABLE();//�����Ϣ����.
			
			//����Ҫ��ʱ���µ���.
			DOWNTABLE.setValueAt(saveFileName, row, 1);//�ļ���
			//��������ͼ��.���Ա������̸߳���.
			DOWNTABLE.setValueAt(new XDownTable().getImageIcon("/image/ing.png"), row, 0);
			DOWNTABLE.setValueAt(fileType, row, 6);//�ļ�����.
			
			
			/**
			 * �۲���������Ľ���,�ٶȱ仯.
			 */
			class ChildObservable extends Observable
			{
				/**
				 *	�۲�ÿ���һ�������ֽڵı仯.
				 * @param count
				 */
				public void compute(long count)
				{
					super.setChanged();  
					super.notifyObservers(count);
				}
			}
			
			class ImObserver implements Observer 
			{
				/**
				 * ����.
				 */
				public void update(Observable o, Object arg)
				{
					//���ؽ���.
					CounterThread.this.getProgress();
					DOWNTABLE.setValueAt(CounterThread.this.downLoadSchedule, row, 2);
					//�����ٶ�.
					DOWNTABLE.setValueAt(CounterThread.this.interceptFolat(count/1024f)+"KB/s", row, 3);
					//����ʱ��.(�ļ�����-�Ѿ�����)/ÿ���ٶ�
					DOWNTABLE.setValueAt(CounterThread.this.getTime((fileLength-readTotal)/((float)count)),row,4);
					//����ʱ��
					DOWNTABLE.setValueAt(CounterThread.this.getTime(((DownLoad)SaveRunTime.getLIST().get(row)).second++), row, 5);
				}
			}
			
			ChildObservable child=new ChildObservable();
			child.addObserver(new ImObserver());
			
			
			for(;;)
			{
				if(inner.timeSign==1) //����״̬.
				{
					try
					{	
						temp=readTotal;
						//�߳�˯��һ��.
						Thread.sleep(1000);
						//�õ�ÿ���������.
						count=readTotal-temp;
						
						child.compute(count);//�۲���.
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					if(inner.timeSign==0) //�ȴ�״̬.�������߳̽���ȴ�֮ǰ֪ͨ.
					{
						synchronized(inner)//��ͣ��ʱ.
						{
							try
							{	//�����Ϣ.
								DOWNTABLE.setValueAt("--", row, 3);
								DOWNTABLE.setValueAt("--", row, 4);
								System.out.println("��ͣ��ʱ");
								inner.wait();	//�߳̽���ȴ�.
								inner.timeSign=1;//����ʱ���ÿ���.
							} catch (InterruptedException e)
							{
								e.printStackTrace();
							}
						}
					}
					else if(inner.timeSign==-1) //�쳣��ֹ״̬.(SocketException ��;����������)
					{	
						DOWNTABLE.setValueAt("--", row, 3);	//�����Ϣ.
						DOWNTABLE.setValueAt("99:0:0", row, 4);
						
						inner.timeSign=1;//ֹ֮ͣ��,���ܻ��ٴα�����.
						try
						{
							new XmlOperation().writerTaskInfo(); //дxml
						} catch (Exception e)
						{
							e.printStackTrace();
						}
						Thread.currentThread().interrupt();//�ж��߳�.
						
						System.out.println("��ʱ�̱߳��ж�..."+Thread.currentThread().isInterrupted());
						return;
					}
					else if(inner.timeSign==-2) //�ֶ�ɾ������.
					{
						DownLoad downLoad=(DownLoad)(SaveRunTime.getLIST().get(row));
						downLoad.taskEnd(row);//ɾ��.
						try
						{
							new XmlOperation().writerTaskInfo(); //ɾ����дxml.
						} catch (Exception e)
						{
							e.printStackTrace();
						}
						Thread.currentThread().interrupt();//�ж��߳�.
						return;
					}
					else if(inner.timeSign==-3) //���������.�������߳�֪ͨ.
					{
						//�������,���������Ϣ.
						DownLoad.this.taskEnd(row);
						try
						{
							new XmlOperation().writerTaskInfo(); 
						} catch (Exception e)
						{
							e.printStackTrace();
						}
						Thread.currentThread().interrupt();//�ж��߳�,����ѭ��
						System.out.println("��ʱ�߳��ж�."+Thread.currentThread().isInterrupted());
						return;
					}
				}
			}
		}
		
		/**
		 * ������������Ľ���.
		 *
		 */
		private void getProgress()
		{
			this.downLoadSchedule= this.interceptFolat((point/(1024f*1024))+readTotal/(1024f*1024))+"M";
		}

		
		/**
		 * ͨ���ϵ�ֵ�õ������������.���з���,���ⲿ����.
		 * @param point
		 * @return
		 */
		public String getProgress(long point)
		{
			return this.interceptFolat(point/(1024f*1024))+"M";
		}
		
		
		/**
		 * ��ʽ��float,��������С����.
		 * @param f
		 * @return
		 */
		private float interceptFolat(float f)
		{
			return new BigDecimal(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).floatValue();
		}
		
		
		/**
		 * ��ʽ��ʱ��:(0:0:0)
		 * @param f
		 * @return
		 */
		public  String getTime(float f)
		{
			int i=(int)f;
			//Сʱ
			int hour=i/3600;
			//��
			int min=(i%3600)/60;
			//��
			int sec=(i%3600)%60;
	
			if(hour>99)
			{
				hour=99;
			}
			return hour+":"+min+":"+sec;
		}
		
	}
	
	
	
	
	/**
	 * ͬ������,���������ʱ����.����һ���߳���������.
	 * @param downLoad
	 */
	private synchronized  void saveTaskEnd(DownLoad downLoad)
	{
		JTable taskEndTable=SaveRunTime.getTASKEND_TABLE();
		
		taskEndTable.setValueAt(new XDownTable().getImageIcon("/image/Ok.png"), TASK_COMPLETE, 0);
		taskEndTable.setValueAt(downLoad.saveFileName, TASK_COMPLETE, 1);
		//���ؽ���.
		taskEndTable.setValueAt(new CounterThread().interceptFolat(downLoad.fileLength/(1024*1024f))+"M", TASK_COMPLETE, 2);
		
		taskEndTable.setValueAt("--", TASK_COMPLETE, 3);
		
		taskEndTable.setValueAt("--", TASK_COMPLETE, 4);
		taskEndTable.setValueAt(new CounterThread().getTime(downLoad.second+0f), TASK_COMPLETE, 5);
		taskEndTable.setValueAt(downLoad.fileType, TASK_COMPLETE, 6);
		TASK_COMPLETE++;//��̬����.
	}
	
	 
	
	/**
	 * ���ڸ����URL�õ����ļ���,��/music/c.mp3 ,ֻȡc.mp3
	 * @param fileName
	 * @return
	 */
	public String interceptURL(String fileName)
	{
		return new StringBuilder(fileName).delete(0, fileName.lastIndexOf("/")+1).toString();
	}
	
	
	
	/**
	 * �����߳�����״̬ʱ����.
	 * @param downLoadSign
	 */
	public  void setDownLoadSign(int downLoadSign)
	{
		this.downLoadSign=downLoadSign;
	}
	
	
	/**
	 *�����̵߳�����״̬. 
	 * @return
	 */
	public int getDownLoadSign()
	{
		return this.downLoadSign;
	}
	
	
	/**
	 * ���ر�����ļ���.
	 * @return
	 */
	public String getDownLoadFileName()	
	{
		return this.saveFileName;
	}
	
	/**
	 * ���ر�����ļ�·��.
	 * @return
	 */
	public String getDownLoadFileDirectory()
	{
		return this.saveDirectory;
	}
	
	
	/**
	 * ���������,��ɾ������ʱ����.����������ֵ.�жϴ���������Χ���߳�����״̬.��������.
	 * @param row
	 */
	public synchronized  void taskEnd(int row)  
	{
		
		List list=SaveRunTime.getLIST();
		if(row==(list.size()-1))//������һ���������.
		{
			//ֱ�������.
			removeLock(row);//ɾ����
			setLastRowNull();//��ɾ����,��������һ��.
			return;
		}
		else
		{
			setRowIndexInfo(row);//���������.
		}
	}
	
	
	
	/**
	 * ���ظ����е��̶߳���.ֻ��setRowIndexInfo()��������.
	 * @param row
	 * @return
	 */
	private DownLoad getRowIndexDownLoadObj(int row)
	{
		return (DownLoad)SaveRunTime.getLIST().get(row);
	}
	
	
	/**
	 * ���������,��ɾ������ʱ��taskEnd()��������.
	 * @param row
	 */
	private synchronized void setRowIndexInfo(int row)
	{
		int rowFirst=row;
		DownLoad downLoad=null;
		JTable DOWNTABLE=SaveRunTime.getDOWN_TABLE();
		for(;row<SaveRunTime.getLIST().size()-1;row++)
		{
			downLoad=getRowIndexDownLoadObj(row+1);//����������һ��.
			
			if(downLoad.downLoadSign==1)//һ���������е��߳�.
			{
				//��������ͼƬ.
				DOWNTABLE.setValueAt(new XDownTable().getImageIcon("/image/ing.png"), row, 0);
				DOWNTABLE.setValueAt("",row,3);//�ٶ�
				DOWNTABLE.setValueAt("",row,4);//ʣ��ʱ��
			}
			else if(downLoad.downLoadSign==0||downLoad.downLoadSign==-3)//�߳��ڵȴ�״̬
			{
				//��ͣͼƬ.
				DOWNTABLE.setValueAt(new XDownTable().getImageIcon("/image/dpause.png"), row, 0);
				DOWNTABLE.setValueAt("--",row,3);//�ٶ�
				DOWNTABLE.setValueAt("--",row,4);//ʣ��ʱ��
			}
			else if(downLoad.downLoadSign==-1)//����ֹͣ��.
			{
				//�����ж�ͼƬ.
				DOWNTABLE.setValueAt(new XDownTable().getImageIcon("/image/die.png"), row, 0);
				DOWNTABLE.setValueAt("--",row,3);//�ٶ�
				DOWNTABLE.setValueAt("--",row,4);//ʣ��ʱ��
			}//����һ�е��������������ƶ�һ��
			DOWNTABLE.setValueAt(DOWNTABLE.getValueAt(row+1, 1), row, 1);//�ļ���.
			DOWNTABLE.setValueAt(DOWNTABLE.getValueAt(row+1, 2), row, 2);//���ؽ��ȱ�.
			DOWNTABLE.setValueAt(DOWNTABLE.getValueAt(row+1, 5), row, 5);//����ʱ��.
			DOWNTABLE.setValueAt(DOWNTABLE.getValueAt(row+1, 6), row, 6);//�ļ�����.
			downLoad.row--;//ÿ���̶߳�Ӧ�ж�Ҫ�����ƶ�һ��λ��.
		}
		removeLock(rowFirst);//�Ƴ�����������.��Ч�м�һ.
		setLastRowNull();//���һ�����.
	}
	
	
	/**
	 * ���ظ����̶߳���ĵ����ӵ�ַ. 
	 * @param downLoad
	 * @return
	 */
	public String getURL(DownLoad downLoad)
	{
		return downLoad.link;
	}
	
	
	/**
	 * ���ظ����̶߳�����ļ�����.
	 * @param downLoad
	 * @return
	 */
	public long getFileLength(DownLoad downLoad)
	{
		return downLoad.fileLength;
	}
	
	/**
	 * ���ظ����̶߳����������. 
	 * @param downLoad
	 * @return
	 */
	public String getHost(DownLoad downLoad)
	{
		return downLoad.hostName;
	}
	
	
	/**
	 *������һ��.���������,��ɾ������ʱ���� 
	 *
	 */
	private void setLastRowNull()
	{
		for(int i=0;i<7;i++)
		{
			SaveRunTime.getDOWN_TABLE().setValueAt("", SaveRunTime.getNEWTASK().getRow(), i);
		}
	}

	
	/**
	 * �Ƴ������е������߳���,��ʱ�߳���,������HashMap�ж�Ӧ��������Ϣ.�����Ч�м�һ.
	 * @param row
	 */
	private synchronized  void removeLock(int row)
	{
		SaveRunTime.getLIST().remove(row);//�߳���,
		SaveRunTime.getTIMELIST().remove(row);//��ʱ��.
		SaveRunTime.getSAVE_HASHMAP().remove(row);//�����һ����Ч������,hashMap��Ҳ����Ų�����������Ĳ�����Ϣ
		SaveRunTime.getNEWTASK().setRow(SaveRunTime.getNEWTASK().getRow()-1);//��Ч�м�һ.
	}
}


/**
 * �߳���,���Ը��������Ƿ���Ч.�����,�����������̺߳��˳�.
 *
 */
class TestLink implements Runnable
{
	private int count=0;//�������Ӵ���,
	private int row=0; //������
	private String url=null;//���ӵ�ַ.
	private String choos=null;//����·��.
	private String saveAs=null;//���Ϊ�ļ���.
	private long point=0;//�ϵ�
	private static JTable tm=SaveRunTime.getDOWN_TABLE();//������.
	private DownLoad downLoad=null;//�����߳�.
	
	/**
	 * ���췽��,�����б�:1,������.2,���ӵ�ַ,3,����·��.4,�����ļ���,5,�ϵ�.
	 * @param row
	 * @param url
	 * @param choos
	 * @param saveAs
	 * @param point
	 */
	public TestLink(int row,String url,String choos,String saveAs,long point)
	{
		this.row=row;
		this.url=url;
		this.choos=choos;
		this.saveAs=saveAs;
		this.point=point;
	}
	
	
	/**
	 * �������ӵ�ַ.�������Ƿ�ȥ���������߳�.
	 */
	public void run()
	{
		System.out.println("����һ�������߳�...");
		//����һ�������߳�.�ȴ����Գɹ�������.
		downLoad=new DownLoad(row,url,choos,saveAs,point);
		
		//���������Ƿ���Ч,���������������.
		if(row==SaveRunTime.getLIST().size()) //���������,�������һ��.
		{	
			SaveRunTime.getLIST().add(downLoad);//�����߳�.
		}
		if(row==SaveRunTime.getTIMELIST().size())//��ʱ�߳�.��Զ�������������еļ�ʱ�̶߳���.
		{														
			SaveRunTime.getTIMELIST().add(downLoad.new CounterThread());
		}
		
		
		//���������ʾ,
		linkError(0);

		boolean f=downLoad.fileInfo();//���������Ƿ���Ч.

		if(f)//���true,����һ���������߳�.�˷���������ʱ,�����߳��ಿ�����Խ�����ֵ.
		{
			linkError(1);
			
			System.out.println("����һ�������߳�.....");
			new Thread(downLoad).start();
			
			try
			{
				Thread.sleep(1000);//˯��һ����дxml.
				new XmlOperation().writerTaskInfo();
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
			
			((DownLoad)SaveRunTime.getLIST().get(row)).setDownLoadSign(1);//���������߳�����״̬.

			Thread.currentThread().interrupt();//�жϲ����߳�.

			System.out.println("�õ�����,�����߳��ж�:"+Thread.currentThread().isInterrupted());
		}
		else
		{
			HashMap<String,String> hm=new HashMap<String,String>(7);//���߸�Ԫ��.
			hm.put("���ڻ�ȡ����...","");
			hm.put("�ļ�����",saveAs);
			hm.put("�ļ�����","δ֪");
			hm.put("�ļ�����","δ֪");
			hm.put("����·��",choos);	//���ڲ���ʮ�κ���Ӽ�ֵ��.��ֹ���Ի�û���,�һ�ɾ�����׳��쳣.
			hm.put("���ӵ�ַ",url);
			hm.put("�Է�����","δ֪");
			SaveRunTime.getSAVE_HASHMAP().add(hm);

			while(count<11)
			{
				try
				{
					f=downLoad.fileInfo();
					if(f)
					{
						new Thread(downLoad).start();
						break;
					}
					//���û�õ�����.��������5��.
					System.out.println("��������"+count+"��");
					Thread.sleep(500);
					if(count==10)
					{
						//��������ʧ����Ϣ.
						linkError(-1);
						try
						{
							//д��xml
							new XmlOperation().writerTaskInfo(); 
						} catch (Exception e)
						{
							e.printStackTrace();
						}
						System.out.println("����ʧ��,�����߳��ж�:"+Thread.currentThread().isInterrupted());
					}
					count++; 
				} catch (InterruptedException e) 
				{
					
				}
			}
			Thread.currentThread().interrupt();//�ж��߳�.
		}
	}


	/**
	 * ���Ʊ����Ϣ�仯.
	 * @param s
	 */
	private void linkError(int s)
	{
		tm.setValueAt(new XDownTable().getImageIcon("/image/ing.png"), row, 0);
		if(s==1)
		{	//�������ӳɹ���Ϣ.
			SaveRunTime.getDOWN_INFOTABLE().setValueAt("���ӳɹ�", 0, 1);
		}
		else if(s==-1)
		{
			//��������ʧ����Ϣ.
			tm.setValueAt(new XDownTable().getImageIcon("/image/die.png"), row, 0);
			SaveRunTime.getDOWN_INFOTABLE().setValueAt("����ʧ��", 0, 1);
		}
		
		//�������ò���.
		tm.setValueAt(saveAs,row,1);
		tm.setValueAt(downLoad.new CounterThread().getProgress(point),row,2);
		tm.setValueAt("--",row,3);
		tm.setValueAt("--",row,4);
	}
}





/**
 * 
 * 	��: 
 * 			ÿ����������Ŀ�ʼ�����������߳�:�����ɲ����̲߳��������Ƿ���Ч,�������������߳�,������һ���ֽں�������ʱ�߳�.
 * 			
 * 			��ʱ�߳������������߳̿���.
 * 
 * 			ÿ������������һ�������߳����ͼ�ʱ�߳���.���ֱ𱣴���ͬ����List��,���˳�������������Ӧ.
 * 
 * 			���ڲ����߳��б����,����Ѿ������������.�������߳�����״̬�Ŀ��ܲ������������е��̶߳���.
 * 			���������쳣�жϺ�,�ٴ����������߳�ʱ��������.ͨ���鿴����downLoadSignֵ��֪������������״̬. 
 * 
 * 		 	�ؼ�����: ������rowIndex,��newTaskDialog�½�����ʱ�����ۼ�.�����������߳��Ƿ������.
 * 
 * 			�����̵߳Ŀ��ر���,�������쳣�жϺ�,������-1ֵ,��ȥ�ı�.�ٴ�����ʱ�ж�.
 * 			��Ӧ�ļ�ʱ�߳̿��ر���,��ǿ���жϺ����óɿ�����״̬.
 * 
 * 			һ������������ɺ�������ֵ��һ,��Ҫ��list���Ƴ�һ��Ԫ��.���������ж��������������һ.
 * 				
 * 			����ͨ��SaveRunTime��õ��Ķ���,��Ӧ����ʹ��ǰ��ֵ.
 * 
 * 			��ȡ����ʷ�����Ĵ���, ��ʷ����������conditionֵ Ϊ-3ʱ����ͼ����Ϊ��ͣ״̬,���������߳���ͣ���ֿ���.
 * 			-1ʱΪ�Ͽ�״̬,��������.
 * 			
 * 			�����̻߳�û���˳�ʱ,�����Ҽ�ɾ��������HashMapֵΪ��ʱ�׳����쳣.(δ���)
 * 
 * 			���������ʷ����ʱ��Ҫ������¼�������: 
 * 			1.������������������߳����ͼ�ʱ�߳���.��������߳���ʱ��Ҫ����DownLoad�Ĵ��ι��췽��,���������Ը�ֵ.
 * 			  ��ʱ�߳���,ֱ��new.
 * 			2.DOWNINFOTABLE��Ӧ��hashMap��Ӽ�ֵ��.
 * 			3.NewTask������rowIndexֵ������ʷ����ĸ���.
 * 
 * 		
 * 			
 * 			
 * 					
 */

