package rmi.mensagem.implmsg;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JOptionPane;

import rmi.chat.msg.Mensagem;

public class MensagemImpl extends UnicastRemoteObject implements Mensagem
{
	private static final long serialVersionUID = 1L;
	private ArrayList<String> mensagens = new ArrayList<String>();
	private ArrayList<String> horario = new ArrayList<String>();
	private ArrayList<String> usuarios = new ArrayList<String>();
	
	private ArrayList<String> destinatario = new ArrayList<String>();
	private ArrayList<String> remetente = new ArrayList<String>();
	
	private ArrayList<String> logTempHoraRecebeu = new ArrayList<String>();
	private ArrayList<String> logTempMinutoRecebeu = new ArrayList<String>();
	
	
	public MensagemImpl() throws RemoteException
	{	
		super();
		//usuarios.add("todos");
	}
	
	
	@Override
	public boolean adicionarUsuario(String usuario) throws RemoteException				//Tem que executar este método antes dos outros
	{	
		boolean inseriu = false;
		boolean naLista = false;
		for(int indice = 0; indice < this.usuarios.size(); indice++)					//Checa se não tem cliente repetido
		{
			if(this.usuarios.get(indice).equals(usuario))
			{
				naLista = true;
				break;
			}
		}

		if(!naLista)
		{
			this.usuarios.add(usuario);
			inseriu = true;
			Calendar c = Calendar.getInstance();
			logTempHoraRecebeu.add(""+c.get(Calendar.HOUR));
			logTempMinutoRecebeu.add(""+c.get(Calendar.MINUTE));
		}
		return inseriu;
	}
	
	
	@Override
	public void removerUsuario(String usuario)
	{
		if(usuarios.contains(usuario))
		{
			for(int indice = 0; indice < usuarios.size(); indice++)
			{
				if(usuarios.get(indice).equals(usuario))
				{
					usuarios.remove(indice);
					logTempHoraRecebeu.remove(indice);
					logTempMinutoRecebeu.remove(indice);
				}
			}
		}
	}
	
	
	@Override
	public String receberMensagem(String usuario, int indice) throws RemoteException
	{	
		String mensagem = "";
		if(destinatario.get(indice).equals(usuario) || remetente.get(indice).equals(usuario) || destinatario.get(indice).equals("todos"))
			 mensagem = mensagens.get(indice)+"\n";
		
		
		
		Calendar c = Calendar.getInstance();
		for(int index = 0; index < usuarios.size(); index++)
		{
			if(usuarios.get(index).equals(usuario))
			{
				logTempHoraRecebeu.set(index, ""+c.get(Calendar.HOUR));
				logTempMinutoRecebeu.set(index, ""+c.get(Calendar.MINUTE));
				//JOptionPane.showMessageDialog(null, usuario+" verificou:"+c.get(Calendar.HOUR)+":"+c.get(Calendar.MINUTE));
				break;
			}
		}
		
		return mensagem;
	}
	

	
	@Override
	public int enviarMensagem(String remetente, String mensagem, String destinatario) throws RemoteException
	{	
		if(destinatario.equals(remetente))
			destinatario = "todos";
		Calendar c = Calendar.getInstance();
		String horario = c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE);
		this.mensagens.add("["+horario+"] "+remetente+": "+mensagem);
		this.horario.add(horario);
		this.destinatario.add(destinatario);
		this.remetente.add(remetente);
		return mensagens.size();
	}
	
	public int enviarMensagem(String remetente, String mensagem, String[] destinatarios) throws RemoteException
	{
		for(int indice = 0; indice < destinatarios.length; indice++)
			enviarMensagem(remetente, mensagem, destinatarios[indice]);
		
		return mensagens.size();
	}

	
	@Override
	public int receberQuantidadeMensagens(String usuario) throws RemoteException
	{	
		Calendar c = Calendar.getInstance();
		for(int index = 0; index < usuarios.size(); index++)
		{
			if(usuarios.get(index).equals(usuario))
			{
				logTempHoraRecebeu.set(index, ""+c.get(Calendar.HOUR));
				logTempMinutoRecebeu.set(index, ""+c.get(Calendar.MINUTE));
				break;
			}
		}
		return mensagens.size();
	}


	@Override
	public String[] getListaUsuarios() throws RemoteException 							//Retorna os usuários já "cadastrados"
	{	
		String [] usuarios = new String[this.usuarios.size()];
		for(int indice = 0; indice < this.usuarios.size(); indice++)
		{
			usuarios[indice] = this.usuarios.get(indice);
		}
		return usuarios;
	}
	
	
	@Override
	public void checarUsuarios() throws RemoteException
	{
		int hora, minuto, horaAtual, minutoAtual, diferencaHora, diferencaMinuto;
		Calendar c = Calendar.getInstance(); 
		horaAtual = c.get(Calendar.HOUR);
		minutoAtual = c.get(Calendar.MINUTE);
		
		ArrayList<String> usuariosInativos = new ArrayList<String>();
		try{
			for(int indice = 0; indice < usuarios.size(); indice++)
			{
				//JOptionPane.showMessageDialog(null, "Indice:"+indice);
				hora = Integer.parseInt(logTempHoraRecebeu.get(indice));
				minuto = Integer.parseInt(logTempMinutoRecebeu.get(indice));
				
				
				diferencaHora = horaAtual - hora;
				diferencaMinuto = minutoAtual - minuto;
				if(diferencaHora < 0)
					diferencaHora *= -1;
				if(diferencaMinuto < 0)
					diferencaMinuto *= -1;
				
							
				if((diferencaHora >= 0 && diferencaMinuto >= 2))
					usuariosInativos.add(usuarios.get(indice));	
			}
			
			for(int indice = 0; indice < usuariosInativos.size(); indice++)
			{
				removerUsuario(usuariosInativos.get(indice));
				/*usuarios.remove(usuariosInativos.get(indice));
				logTempHoraRecebeu.remove(usuariosInativos.get(indice));
				logTempMinutoRecebeu.remove(usuariosInativos.get(indice));*/
			}
		}
		catch(Exception e)
		{	e.printStackTrace();	}
	}
}