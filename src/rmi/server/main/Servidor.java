package rmi.server.main;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

import rmi.chat.msg.Mensagem;
import rmi.mensagem.implmsg.MensagemImpl;

public class Servidor //extends ServerMainBase
{
	public static void main(String []args) throws RemoteException
	{
		MensagemImpl msg;
		String port = "1090";
		String ip = "localhost";
		
		ArrayList<Socket> skClientes = new ArrayList<Socket>();
		ArrayList<String> stClientes = new ArrayList<String>();
		
		
		if(args.length == 2)
		{
			ip = args[0];
			port = args[1];
		}
		else
		{
			ip = JOptionPane.showInputDialog(null,"Informe o ip","localhost");
			port = JOptionPane.showInputDialog(null, "Informe a porta: ", "1090");
		}	
		
		try
		{
			System.setProperty("java.rmi.server.hostname",""+ip);
			msg = new MensagemImpl();
			Registry registry = LocateRegistry.createRegistry(Integer.parseInt(port));
			//registry.rebind("//10.0.0.100:"+port+"/MensagemImpl", msg);
			Naming.bind("//"+ip+":"+port+"/MensagemImpl", msg);
			System.out.println("Server ativo");
			
			
			Thread checarOnline = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					//try 
					//{	
						while(true)
						{
							try
							{
								msg.checarUsuarios();
							}
							catch(Exception e)
							{	e.printStackTrace();	}
						}
					//} 
					//catch (RemoteException e) 
					//{	System.err.println(e.toString());	}
				}
			});
			checarOnline.start();
			/*while(true)
			{
				checarOnline.start();
				checarOnline.sleep(1000);
			}*/
		}
		catch(Exception e)
		{	e.printStackTrace();	}
	}
}
