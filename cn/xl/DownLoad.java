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
 * 线程类,下载实现.
 */
class DownLoad extends Thread
{
	
	private long  readTotal=0;	//已读的字节数.
	private long point=0;//断点.
	private String fileName="";//文件名.
	private long fileLength=0;//文件长度.
	private String hostName="";//对方主机名.
	private String fileType="";  //部分属性应该设置为 "",而非null,避免抛出异常.
	private String link="";	//链接地址.
	public  int row=0;			//行索引.
	private String saveFileName="";//另存为文件名.
	private String saveDirectory="";//保存路径.
	
	
	private URL url=null;//链接类.
	private URLConnection URLC=null;
	
	//输入输出流.
	private InputStream ins=null;
	private DataInputStream  datainput=null;
	
	
	/**
	 * 下载线程控制.1为运行状态,0为等待,-1为中断(适用于中断的历史任务).-2为手动删除.-3为加载正常历史记录.
	 */
	private int downLoadSign=-1;
	
	/**
	 * 已完成的任务数.只有saveTaskEnd()方法使用此静态变量.
	 */
	private static int TASK_COMPLETE=0;
	
	/**
	 * 已用时间.由线程锁保存其值.
	 */
	public long second=0;
	
	
	
	
	/**
	 * 默认构造方法.
	 */	
	public DownLoad(){}
	
	
	/**
	 * 构造方法,新建下载任务时调用,参数包括 1,行号索引.2,链接路径,3,文件保存路径,4,被保存的文件名. 5,断点.
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
	 *下载实现. 
	 */
	public void run()
	{	
		
		DownLoad downLoad=(DownLoad)SaveRunTime.getLIST().get(row);//下载线程锁.
		downLoad.downLoadSign=1;//开关.线程处于可运行状态.
		CounterThread counter=(CounterThread)SaveRunTime.getTIMELIST().get(row);//计时锁.
		JTable DOWNTABLE=SaveRunTime.getDOWN_TABLE();//表对象.控制表格变化.
		try
		{
			datainput=new DataInputStream(ins);//二进制输入流.
			RandomAccessFile raf=new RandomAccessFile(saveDirectory+saveFileName,"rw");//输出流.
			raf.seek(point);//指针移至断点处.
			int by=0;
			
			try
			{
				by=datainput.read();//读一个字节.
			}
			catch (SocketException e)//可能抛出异常,只能中断线程,
			{
				//读不到数据.通知线程需要被中断.
				System.out.println("读不到数据.");
				downLoad.downLoadSign=-1;//下载线程将被中断.
			}
			
			
			System.out.println("文件长度:"+fileLength);
			System.out.println("启动一个计时线程...");
			
			
			CounterThread count_Speed=new CounterThread();
			new Thread(count_Speed).start();//读到一个字节后启动计时线程.
			
			SaveRunTime.getSPEED_LIST().add(count_Speed);//保存正在运行中的计时线程对象.
			while(by!=-1)
			{
				if(downLoad.downLoadSign!=1)
				{				
					synchronized(downLoad)
					{		
						if(downLoad.downLoadSign==0)//线程进入等待.
						{
							counter.timeSign=0; //通知计时线程进入等待.
							
							count_Speed.timeSign=-1;//通知即时速度计算器,不计算此任务的速度.
							
							downLoad.wait();//下载线程进入等待.
							
							synchronized (counter)//醒来时唤醒计时线程.
							{
								counter.notify();
							}
							//设置下载图标.
							DOWNTABLE.setValueAt(new XDownTable().getImageIcon("/image/ing.png"), row, 0);
							downLoad.downLoadSign=1;//设置开关.
							
							count_Speed.timeSign=1;
						}
						else if(downLoad.downLoadSign==-1)//线程需要被中断.下载异常中断时调用.
						{	
							//连接中断图标.
							DOWNTABLE.setValueAt(new XDownTable().getImageIcon("/image/die.png"), row, 0);
							
							count_Speed.timeSign=-1;
							
							counter.timeSign=-1;//通知计时线程中断.
							Thread.currentThread().interrupt();//下载线程被中断.
							System.out.println("得不到链接,下载线程中断."+Thread.currentThread().isInterrupted());
							return;
						}
						else if(downLoad.downLoadSign==-2)//手动强制删除任务,只有当任务在运行时才被调用.
						{
							count_Speed.timeSign=-1;
							
							counter.timeSign=-2; //通知计时线程需要中断.
							Thread.currentThread().interrupt();//下载线程被中断.
							return;
						}
					}
				}
				try
				{
					raf.write(by);//写一个字节.
					by=datainput.read();//读一个字节,这里会抛出SocketExcetpion.
					readTotal++;//累计读到的字节,用于计时线程计算进度.
				}
				catch (SocketException e)
				{
					//读不到数据.通知线程需要被中断.
					System.out.println("读不到数据.");
					downLoad.downLoadSign=-1;//下载线程将被中断.
				}
			}
			//for循环完成.
			datainput.close();
			raf.close();//关闭流.
			
			saveTaskEnd(downLoad);//保存完成的任务信息到 已下载表.
			count_Speed.timeSign=-1;//通知即时速度计算器.
			counter.timeSign=-3;    //通知计时线程,下载完成.
			Thread.currentThread().interrupt();//中断线程.
			System.out.println("下载线程中断:"+Thread.currentThread().isInterrupted());
			System.out.println("下载完成");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	
	/**
	 *  单个下载任务的基本信息.得到连接时返回true ,此方法会给类的字段赋值.
	 *	包括.link,url,URLC,ins,fileName,fileLength,fileType,hostName.
	 *	如果下载线程没有启动,则部分字段值为null,
	 *	方法由测试线程调用.
	 */
	public boolean fileInfo()
	{
		JTable DOWNINFO_TABLE=SaveRunTime.getDOWN_INFOTABLE();
		DOWNINFO_TABLE.setValueAt("正在获取连接......", 0, 0);
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
			URLC=url.openConnection();//得到链接.		//断点.
			URLC.setRequestProperty("RANGE", "bytes="+point+"-");
			ins=URLC.getInputStream();
		
			//System.out.println(URLC.getHeaderFields());393696992
			
			
			//文件名.直接取自链接地址.
			fileName=interceptURL(url.getFile());
			DOWNINFO_TABLE.setValueAt("文件名称", 1, 0);
			DOWNINFO_TABLE.setValueAt(fileName, 1, 1);
			//文件长度.
			fileLength=URLC.getContentLength()+point;
			float f=((float)fileLength)/((float)(1024*1024)+(point/(1024f*1024f)));
			DOWNINFO_TABLE.setValueAt("文件长度", 2, 0);
			DOWNINFO_TABLE.setValueAt(new CounterThread().interceptFolat(f)+"M", 2, 1);
			//文件类型.
			fileType=URLC.getContentType();
			if(fileType==null||"".equals(fileType))
			{
				fileType="未知";
			}
			DOWNINFO_TABLE.setValueAt("文件类型", 3, 0);
			DOWNINFO_TABLE.setValueAt(fileType, 3, 1);
			//保存路径.
			DOWNINFO_TABLE.setValueAt("保存路径", 4, 0);
			DOWNINFO_TABLE.setValueAt(saveDirectory, 4, 1);
			//链接地址.
			DOWNINFO_TABLE.setValueAt("链接地址", 5, 0);
			DOWNINFO_TABLE.setValueAt(link, 5, 1);
			//对方主机名.
			hostName=url.getHost();
			DOWNINFO_TABLE.setValueAt("对方主机", 6, 0);
			DOWNINFO_TABLE.setValueAt(hostName, 6, 1);
			
		
			
			
			//没有异常情况时;添加键值对.
			HashMap<String,String> hm=new HashMap<String,String>(7);
			hm.put("正在获取链接...","");
			hm.put("文件名称",saveFileName);
			hm.put("文件长度",new CounterThread().interceptFolat(f)+"M");
			hm.put("文件类型",fileType);
			hm.put("保存路径",saveDirectory+saveFileName);
			hm.put("链接地址",link);
			hm.put("对方主机",hostName);
			SaveRunTime.getSAVE_HASHMAP().add(hm);
			return true;
		}
		catch (Exception e)
		{	
			return false;
		}
	}
	
	
	/**
	 * 线程类,计算下载器的即时速度.程序最早开始运行时启动此线程.一直运行至程序被关闭.
	 */
	class CounterSpeed implements Runnable
	{
		
		public void run()
		{
			/* List中保存的是计时线程对象.但并不用于控制计时.所以在程序运行时对timeSign的属性值的改变,仅仅是
			 * 通知即时速度计算器决定是否计算此线程的下载速度.
			 * */
			long speed=0;
			for(;;)
			{
				List<Object> counter=SaveRunTime.getSPEED_LIST();//对象在启动计时线程后被保存.
				for(int i=0;i<counter.size();i++)
				{	
					CounterThread count_Speed=(CounterThread)counter.get(i);
					if(count_Speed.timeSign==1)
					{
						speed=speed+count_Speed.count;
					}
				}
				//控制Jlabel的变化.
				SaveRunTime.getSPEED_JLABEL().setText("\u901f\u5ea6:"+new CounterThread().interceptFolat(speed/(1024f))+"KB/S");
				speed=0;//归零.
				try
				{
					Thread.sleep(500);//睡眠半秒.
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	/**
	 * 线程类,由下载线程调用.用于计算单个任务的 进度,速度,剩余时间,已用时间.
	 *
	 */
	class CounterThread implements Runnable
	{
		/**
		 *线程控制开关.四个有效值:1.运行.0,等待.-1,中断.-2,手动删除.-3,任务完成.
		 */
		public int timeSign=1;
		
		/**
		 * 每隔一秒,计算差值.得到下载速度.
		 */
		private long count=0; 
		
		/**
		 * 临时变量.保存一秒之前的读到字节数.
		 */
		private long temp=0;
		
		/**
		 * 任务进度.
		 */
		private String downLoadSchedule=null;
		
		/**
		 * 具体实现,下载线程读到第一个字节后启动.
		 */
		public void run()
		{								
			CounterThread inner=(CounterThread)SaveRunTime.getTIMELIST().get(row);//锁.
			final JTable DOWNTABLE=SaveRunTime.getDOWN_TABLE();//表格信息控制.
			
			//不需要随时更新的列.
			DOWNTABLE.setValueAt(saveFileName, row, 1);//文件名
			//正在下载图标.可以被下载线程更改.
			DOWNTABLE.setValueAt(new XDownTable().getImageIcon("/image/ing.png"), row, 0);
			DOWNTABLE.setValueAt(fileType, row, 6);//文件类型.
			
			
			/**
			 * 观察下载任务的进度,速度变化.
			 */
			class ChildObservable extends Observable
			{
				/**
				 *	观察每间隔一秒后读到字节的变化.
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
				 * 更新.
				 */
				public void update(Observable o, Object arg)
				{
					//下载进度.
					CounterThread.this.getProgress();
					DOWNTABLE.setValueAt(CounterThread.this.downLoadSchedule, row, 2);
					//下载速度.
					DOWNTABLE.setValueAt(CounterThread.this.interceptFolat(count/1024f)+"KB/s", row, 3);
					//所需时间.(文件长度-已经下载)/每秒速度
					DOWNTABLE.setValueAt(CounterThread.this.getTime((fileLength-readTotal)/((float)count)),row,4);
					//已用时间
					DOWNTABLE.setValueAt(CounterThread.this.getTime(((DownLoad)SaveRunTime.getLIST().get(row)).second++), row, 5);
				}
			}
			
			ChildObservable child=new ChildObservable();
			child.addObserver(new ImObserver());
			
			
			for(;;)
			{
				if(inner.timeSign==1) //运行状态.
				{
					try
					{	
						temp=readTotal;
						//线程睡眠一秒.
						Thread.sleep(1000);
						//得到每秒的下载量.
						count=readTotal-temp;
						
						child.compute(count);//观察者.
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					if(inner.timeSign==0) //等待状态.由下载线程进入等待之前通知.
					{
						synchronized(inner)//暂停计时.
						{
							try
							{	//表格信息.
								DOWNTABLE.setValueAt("--", row, 3);
								DOWNTABLE.setValueAt("--", row, 4);
								System.out.println("暂停计时");
								inner.wait();	//线程进入等待.
								inner.timeSign=1;//醒来时设置开关.
							} catch (InterruptedException e)
							{
								e.printStackTrace();
							}
						}
					}
					else if(inner.timeSign==-1) //异常中止状态.(SocketException 中途读不到数据)
					{	
						DOWNTABLE.setValueAt("--", row, 3);	//表格信息.
						DOWNTABLE.setValueAt("99:0:0", row, 4);
						
						inner.timeSign=1;//停止之后,可能会再次被启动.
						try
						{
							new XmlOperation().writerTaskInfo(); //写xml
						} catch (Exception e)
						{
							e.printStackTrace();
						}
						Thread.currentThread().interrupt();//中断线程.
						
						System.out.println("计时线程被中断..."+Thread.currentThread().isInterrupted());
						return;
					}
					else if(inner.timeSign==-2) //手动删除任务.
					{
						DownLoad downLoad=(DownLoad)(SaveRunTime.getLIST().get(row));
						downLoad.taskEnd(row);//删除.
						try
						{
							new XmlOperation().writerTaskInfo(); //删除后写xml.
						} catch (Exception e)
						{
							e.printStackTrace();
						}
						Thread.currentThread().interrupt();//中断线程.
						return;
					}
					else if(inner.timeSign==-3) //有任务完成.由下载线程通知.
					{
						//下载完成,清空行内信息.
						DownLoad.this.taskEnd(row);
						try
						{
							new XmlOperation().writerTaskInfo(); 
						} catch (Exception e)
						{
							e.printStackTrace();
						}
						Thread.currentThread().interrupt();//中断线程,跳出循环
						System.out.println("计时线程中断."+Thread.currentThread().isInterrupted());
						return;
					}
				}
			}
		}
		
		/**
		 * 计算下载任务的进度.
		 *
		 */
		private void getProgress()
		{
			this.downLoadSchedule= this.interceptFolat((point/(1024f*1024))+readTotal/(1024f*1024))+"M";
		}

		
		/**
		 * 通过断点值得到下载任务进度.公有方法,被外部调用.
		 * @param point
		 * @return
		 */
		public String getProgress(long point)
		{
			return this.interceptFolat(point/(1024f*1024))+"M";
		}
		
		
		/**
		 * 格式化float,保留两个小数点.
		 * @param f
		 * @return
		 */
		private float interceptFolat(float f)
		{
			return new BigDecimal(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).floatValue();
		}
		
		
		/**
		 * 格式化时间:(0:0:0)
		 * @param f
		 * @return
		 */
		public  String getTime(float f)
		{
			int i=(int)f;
			//小时
			int hour=i/3600;
			//分
			int min=(i%3600)/60;
			//秒
			int sec=(i%3600)%60;
	
			if(hour>99)
			{
				hour=99;
			}
			return hour+":"+min+":"+sec;
		}
		
	}
	
	
	
	
	/**
	 * 同步方法,有任务完成时调用.传递一个线程锁作参数.
	 * @param downLoad
	 */
	private synchronized  void saveTaskEnd(DownLoad downLoad)
	{
		JTable taskEndTable=SaveRunTime.getTASKEND_TABLE();
		
		taskEndTable.setValueAt(new XDownTable().getImageIcon("/image/Ok.png"), TASK_COMPLETE, 0);
		taskEndTable.setValueAt(downLoad.saveFileName, TASK_COMPLETE, 1);
		//下载进度.
		taskEndTable.setValueAt(new CounterThread().interceptFolat(downLoad.fileLength/(1024*1024f))+"M", TASK_COMPLETE, 2);
		
		taskEndTable.setValueAt("--", TASK_COMPLETE, 3);
		
		taskEndTable.setValueAt("--", TASK_COMPLETE, 4);
		taskEndTable.setValueAt(new CounterThread().getTime(downLoad.second+0f), TASK_COMPLETE, 5);
		taskEndTable.setValueAt(downLoad.fileType, TASK_COMPLETE, 6);
		TASK_COMPLETE++;//静态变量.
	}
	
	 
	
	/**
	 * 用于改造从URL得到的文件名,如/music/c.mp3 ,只取c.mp3
	 * @param fileName
	 * @return
	 */
	public String interceptURL(String fileName)
	{
		return new StringBuilder(fileName).delete(0, fileName.lastIndexOf("/")+1).toString();
	}
	
	
	
	/**
	 * 控制线程运行状态时调用.
	 * @param downLoadSign
	 */
	public  void setDownLoadSign(int downLoadSign)
	{
		this.downLoadSign=downLoadSign;
	}
	
	
	/**
	 *返回线程的运行状态. 
	 * @return
	 */
	public int getDownLoadSign()
	{
		return this.downLoadSign;
	}
	
	
	/**
	 * 返回保存的文件名.
	 * @return
	 */
	public String getDownLoadFileName()	
	{
		return this.saveFileName;
	}
	
	/**
	 * 返回保存的文件路径.
	 * @return
	 */
	public String getDownLoadFileDirectory()
	{
		return this.saveDirectory;
	}
	
	
	/**
	 * 有任务完成,或删除任务时调用.传递行索引值.判断此索引行周围的线程运行状态.作出处理.
	 * @param row
	 */
	public synchronized  void taskEnd(int row)  
	{
		
		List list=SaveRunTime.getLIST();
		if(row==(list.size()-1))//如果最后一个任务完成.
		{
			//直接清空行.
			removeLock(row);//删除锁
			setLastRowNull();//先删除锁,再清空最后一行.
			return;
		}
		else
		{
			setRowIndexInfo(row);//身后还有任务.
		}
	}
	
	
	
	/**
	 * 返回给定行的线程对象.只被setRowIndexInfo()方法调用.
	 * @param row
	 * @return
	 */
	private DownLoad getRowIndexDownLoadObj(int row)
	{
		return (DownLoad)SaveRunTime.getLIST().get(row);
	}
	
	
	/**
	 * 有任务完成,或删除任务时由taskEnd()方法调用.
	 * @param row
	 */
	private synchronized void setRowIndexInfo(int row)
	{
		int rowFirst=row;
		DownLoad downLoad=null;
		JTable DOWNTABLE=SaveRunTime.getDOWN_TABLE();
		for(;row<SaveRunTime.getLIST().size()-1;row++)
		{
			downLoad=getRowIndexDownLoadObj(row+1);//紧挨着身后的一行.
			
			if(downLoad.downLoadSign==1)//一个正在运行的线程.
			{
				//正在运行图片.
				DOWNTABLE.setValueAt(new XDownTable().getImageIcon("/image/ing.png"), row, 0);
				DOWNTABLE.setValueAt("",row,3);//速度
				DOWNTABLE.setValueAt("",row,4);//剩余时间
			}
			else if(downLoad.downLoadSign==0||downLoad.downLoadSign==-3)//线程在等待状态
			{
				//暂停图片.
				DOWNTABLE.setValueAt(new XDownTable().getImageIcon("/image/dpause.png"), row, 0);
				DOWNTABLE.setValueAt("--",row,3);//速度
				DOWNTABLE.setValueAt("--",row,4);//剩余时间
			}
			else if(downLoad.downLoadSign==-1)//任务停止中.
			{
				//链接中断图片.
				DOWNTABLE.setValueAt(new XDownTable().getImageIcon("/image/die.png"), row, 0);
				DOWNTABLE.setValueAt("--",row,3);//速度
				DOWNTABLE.setValueAt("--",row,4);//剩余时间
			}//将下一行的内容设置向上移动一行
			DOWNTABLE.setValueAt(DOWNTABLE.getValueAt(row+1, 1), row, 1);//文件名.
			DOWNTABLE.setValueAt(DOWNTABLE.getValueAt(row+1, 2), row, 2);//下载进度表.
			DOWNTABLE.setValueAt(DOWNTABLE.getValueAt(row+1, 5), row, 5);//已用时间.
			DOWNTABLE.setValueAt(DOWNTABLE.getValueAt(row+1, 6), row, 6);//文件类型.
			downLoad.row--;//每个线程对应行都要向上移动一个位置.
		}
		removeLock(rowFirst);//移除完成任务的锁.有效行减一.
		setLastRowNull();//最后一行清空.
	}
	
	
	/**
	 * 返回给定线程对象的的链接地址. 
	 * @param downLoad
	 * @return
	 */
	public String getURL(DownLoad downLoad)
	{
		return downLoad.link;
	}
	
	
	/**
	 * 返回给定线程对象的文件长度.
	 * @param downLoad
	 * @return
	 */
	public long getFileLength(DownLoad downLoad)
	{
		return downLoad.fileLength;
	}
	
	/**
	 * 返回给定线程对象的主机名. 
	 * @param downLoad
	 * @return
	 */
	public String getHost(DownLoad downLoad)
	{
		return downLoad.hostName;
	}
	
	
	/**
	 *清空最后一行.有任务完成,或删除任务时调用 
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
	 * 移除给定行的下载线程锁,计时线程锁,保存在HashMap中对应的任务信息.表格有效行减一.
	 * @param row
	 */
	private synchronized  void removeLock(int row)
	{
		SaveRunTime.getLIST().remove(row);//线程锁,
		SaveRunTime.getTIMELIST().remove(row);//计时锁.
		SaveRunTime.getSAVE_HASHMAP().remove(row);//如果是一个无效的链接,hashMap中也将存放部着下载任务的部分信息
		SaveRunTime.getNEWTASK().setRow(SaveRunTime.getNEWTASK().getRow()-1);//有效行减一.
	}
}


/**
 * 线程类,测试给定链接是否有效.如果是,在启动下载线程后退出.
 *
 */
class TestLink implements Runnable
{
	private int count=0;//测试链接次数,
	private int row=0; //行索引
	private String url=null;//链接地址.
	private String choos=null;//保存路径.
	private String saveAs=null;//另存为文件名.
	private long point=0;//断点
	private static JTable tm=SaveRunTime.getDOWN_TABLE();//表格对象.
	private DownLoad downLoad=null;//下载线程.
	
	/**
	 * 构造方法,参数列表:1,行索引.2,链接地址,3,保存路径.4,保存文件名,5,断点.
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
	 * 测试链接地址.并决定是否去启动下载线程.
	 */
	public void run()
	{
		System.out.println("启动一个测试线程...");
		//创建一个下载线程.等待测试成功后启动.
		downLoad=new DownLoad(row,url,choos,saveAs,point);
		
		//不管链接是否有效,往集合类中添加锁.
		if(row==SaveRunTime.getLIST().size()) //如果是新行,将是最后一行.
		{	
			SaveRunTime.getLIST().add(downLoad);//下载线程.
		}
		if(row==SaveRunTime.getTIMELIST().size())//计时线程.永远不会是正在运行的计时线程对象.
		{														
			SaveRunTime.getTIMELIST().add(downLoad.new CounterThread());
		}
		
		
		//表格内容显示,
		linkError(0);

		boolean f=downLoad.fileInfo();//测试连接是否有效.

		if(f)//如果true,启动一个下载下线程.此方法被调用时,下载线程类部分属性将被赋值.
		{
			linkError(1);
			
			System.out.println("启动一个下载线程.....");
			new Thread(downLoad).start();
			
			try
			{
				Thread.sleep(1000);//睡眠一秒再写xml.
				new XmlOperation().writerTaskInfo();
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
			
			((DownLoad)SaveRunTime.getLIST().get(row)).setDownLoadSign(1);//设置下载线程运行状态.

			Thread.currentThread().interrupt();//中断测试线程.

			System.out.println("得到连接,测试线程中断:"+Thread.currentThread().isInterrupted());
		}
		else
		{
			HashMap<String,String> hm=new HashMap<String,String>(7);//有七个元素.
			hm.put("正在获取链接...","");
			hm.put("文件名称",saveAs);
			hm.put("文件长度","未知");
			hm.put("文件类型","未知");
			hm.put("保存路径",choos);	//不在测试十次后添加键值对.防止测试还没完成,右击删除键抛出异常.
			hm.put("链接地址",url);
			hm.put("对方主机","未知");
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
					//如果没得到链接.尝试连接5秒.
					System.out.println("尝试连接"+count+"次");
					Thread.sleep(500);
					if(count==10)
					{
						//传递连接失败信息.
						linkError(-1);
						try
						{
							//写入xml
							new XmlOperation().writerTaskInfo(); 
						} catch (Exception e)
						{
							e.printStackTrace();
						}
						System.out.println("连接失败,测试线程中断:"+Thread.currentThread().isInterrupted());
					}
					count++; 
				} catch (InterruptedException e) 
				{
					
				}
			}
			Thread.currentThread().interrupt();//中断线程.
		}
	}


	/**
	 * 控制表格信息变化.
	 * @param s
	 */
	private void linkError(int s)
	{
		tm.setValueAt(new XDownTable().getImageIcon("/image/ing.png"), row, 0);
		if(s==1)
		{	//返回连接成功信息.
			SaveRunTime.getDOWN_INFOTABLE().setValueAt("连接成功", 0, 1);
		}
		else if(s==-1)
		{
			//返回连接失败信息.
			tm.setValueAt(new XDownTable().getImageIcon("/image/die.png"), row, 0);
			SaveRunTime.getDOWN_INFOTABLE().setValueAt("连接失败", 0, 1);
		}
		
		//其它设置不变.
		tm.setValueAt(saveAs,row,1);
		tm.setValueAt(downLoad.new CounterThread().getProgress(point),row,2);
		tm.setValueAt("--",row,3);
		tm.setValueAt("--",row,4);
	}
}





/**
 * 
 * 	附: 
 * 			每个下载任务的开始会启动三个线程:首先由测试线程测试链接是否有效,接着启动下载线程,读到第一个字节后启动计时线程.
 * 			
 * 			计时线程总是由下载线程控制.
 * 
 * 			每个下载任务都有一个下载线程锁和计时线程锁.被分别保存在同步的List中,存放顺序与行索引相对应.
 * 
 * 			锁在测试线程中被添加,如果已经存在则不再添加.即控制线程运行状态的可能并不是正在运行的线程对象.
 * 			方便下载异常中断后,再次重新启动线程时继续控制.通过查看锁的downLoadSign值得知下载任务运行状态. 
 * 
 * 		 	关键变量: 行索引rowIndex,在newTaskDialog新建任务时进行累加.而不管下载线程是否会运行.
 * 
 * 			下载线程的开关变量,在碰到异常中断后,保留其-1值,不去改变.再次启动时判断.
 * 			对应的计时线程开关变量,在强制中断后设置成可运行状态.
 * 
 * 			一个下载任务完成后行索引值减一,需要在list中移除一个元素.其身后的所有对象的行索引都减一.
 * 				
 * 			所有通过SaveRunTime类得到的对象,都应该在使用前赋值.
 * 
 * 			读取到历史任务后的处理, 历史任务中任务condition值 为-3时任务图标设为暂停状态,与真正的线程暂停区分开来.
 * 			-1时为断开状态,不必区分.
 * 			
 * 			测试线程还没有退出时,避免右键删除带来的HashMap值为空时抛出的异常.(未完成)
 * 
 * 			程序加载历史任务时需要完成以下几个动作: 
 * 			1.给集合类中添加下载线程锁和计时线程锁.添加下载线程锁时需要调用DownLoad的带参构造方法,给部分属性赋值.
 * 			  计时线程锁,直接new.
 * 			2.DOWNINFOTABLE对应的hashMap添加键值对.
 * 			3.NewTask的属性rowIndex值等于历史任务的个数.
 * 
 * 		
 * 			
 * 			
 * 					
 */

