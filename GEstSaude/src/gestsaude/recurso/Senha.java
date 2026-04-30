package gestsaude.recurso;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma Senha. Deve ter um número (letra e número, por exemplo A1, A2,
 * A12), a hora de entrada e a hora prevista de atendimento e qual a consulta
 * associada.
 */
public class Senha {

	private static int contadorSenhas = 1; // gerador do número da senha

	private GEstSaude gest;            // ligação ao sistema
	private String numero;             // número da senha, ex: "A1", "A12"
	private Consulta consulta;         // consulta associada
	private LocalDateTime entrada;     // hora em que o utente entrou
	private LocalDateTime atendimento; // hora prevista de atendimento
	private List<Servico> servicos;    // lista de serviços a visitar por ordem

	public Senha(GEstSaude gest, Consulta consulta, LocalDateTime entrada, LocalDateTime atendimento) {
		this.gest = gest;
		this.consulta = consulta;
		this.entrada = entrada;
		this.atendimento = atendimento;
		this.numero = "A" + contadorSenhas++;
		this.servicos = new ArrayList<>();
	}

	/**
	 * Reinicia o contador de senhas (chamado no início de cada dia)
	 */
	public static void reiniciaContador() {
		contadorSenhas = 1;
	}

	public String getNumero() {
		return numero;
	}

	public Consulta getConsulta() {
		return consulta;
	}

	public LocalDateTime getEntrada() {
		return entrada;
	}

	public LocalDateTime getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(LocalDateTime atendimento) {
		if (atendimento != null)
			this.atendimento = atendimento;
	}

	/**
	 * Adiciona um serviço à lista de encaminhamentos, se ainda não existir.
	 *
	 * @param s o serviço a adicionar
	 * @return true se foi adicionado, false se já existia
	 */
	public boolean addServico(Servico s) {
		for (Servico sv : servicos)
			if (sv.getId().equals(s.getId()))
				return false;
		servicos.add(s);
		return true;
	}

	/**
	 * Retorna a lista de serviços a visitar.
	 *
	 * @return lista de serviços por ordem de visita
	 */
	public List<Servico> getServicos() {
		return new ArrayList<>(servicos);
	}

	/**
	 *
	 * @return o serviço atual ou null se não tiver serviços a visitar
	 */
	public Servico servicoAtual() {
		if (servicos.isEmpty())
			return null;
		return servicos.get(0);
	}

	/**
	 * Terminou o atendimento na consulta ou serviço atual. Se ainda tem serviços
	 * por visitar, deve passar para o próximo.
	 */
	public void terminaAtendimento() {
		if (!servicos.isEmpty())
			servicos.remove(0);
		if (servicoAtual() != null)
			servicoAtual().addSenha(this);
		else
			gest.terminaConsulta(consulta);
	}

	@Override
	public String toString() {
		return numero + " - " + consulta.getUtente().getNome();
	}
}