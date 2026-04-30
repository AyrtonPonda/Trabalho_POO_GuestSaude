package gestsaude.menu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import gestsaude.recurso.*;

/**
 * Representa uma máquina de entrada, onde os clientes retiram as senhas
 * Neste caso, apenas permite retirar as senhas para confirmar consulta
 */
public class MaquinaEntrada extends javax.swing.JDialog {

	private static final long serialVersionUID = 1L;
	private static final Dimension tamanhoBt = new Dimension(170, 50); // tamanho dos botões

	private GEstSaude gest;

	/**
	 * Cria uma janela para uma máquina de entrada
	 *
	 * @param posicao onde fica a janela
	 * @param nome    título da janela
	 * @param gs      o sistema a que esta máquina fica ligada
	 */
	public MaquinaEntrada(JFrame owner, Point posicao, String nome, GEstSaude gs) {
		super(owner, "Máquina - " + nome);
		setupAspeto();
		setLocation(posicao);
		gest = gs;
	}

	/** chamado quando se pressiona o botão "Validar Consulta" */
	private void validarConsulta() {
		String numSns = JOptionPane.showInputDialog(this, "Qual o seu número de SNS?");
		if (numSns == null)
			return;

		Utente u = null;
		try {
			u = gest.getUtente(Integer.parseInt(numSns));
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Número inválido");
			return;
		}

		if (u == null) {
			JOptionPane.showMessageDialog(this, "Número inválido");
			return;
		}

		String nomeUtente = u.getNome();
		Consulta c = gest.primeiraConsultaDia(u);
		if (c == null) {
			JOptionPane.showMessageDialog(this, nomeUtente + ", não tem consultas hoje!");
			return;
		}

		int resultado = gest.validarConsulta(c);
		if (resultado == GEstSaude.UTENTE_SEM_CONSULTA_PROXIMA) {
			JOptionPane.showMessageDialog(this, nomeUtente + ", não tem consultas nas próximas 3 horas!");
			return;
		}
		if (resultado == GEstSaude.UTENTE_DEMASIADO_ATRASADO) {
			JOptionPane.showMessageDialog(this,
					nomeUtente + ", atraso na consulta superior ao permitido, a consulta foi anulada!");
			return;
		}
		if (resultado == GEstSaude.UTENTE_ATRASADO_FORAHORAS) {
			JOptionPane.showMessageDialog(this,
					nomeUtente + ", o seu atraso levou a que a consulta fosse anulada!");
			return;
		}
		if (resultado == GEstSaude.UTENTE_ATRASADO_ADIADO) {
			JOptionPane.showMessageDialog(this,
					nomeUtente + ", o seu atraso implicou alterações na hora da consulta!");
			return;
		}

		Senha s = gest.getSenha(c);
		JOptionPane.showMessageDialog(this, nomeUtente + ", a sua senha é " + s.getNumero());
	}

	// métodos relacionados com a interface gráfica. Não deve ser necessário alterar
	// nada nada nestes métodos
	/**
	 * Configura o aspeto visual da janela
	 */
	private void setupAspeto() {
		setLayout(new GridLayout(0, 1));
		JButton validarBt = new JButton("Validar Consulta");
		validarBt.setMinimumSize(tamanhoBt);
		validarBt.setPreferredSize(tamanhoBt);
		validarBt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				validarConsulta();
			}
		});
		add(validarBt);
		pack();
	}
}
