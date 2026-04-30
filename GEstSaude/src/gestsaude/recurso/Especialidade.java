package gestsaude.recurso;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import poo.tempo.HorarioDiario;
import poo.util.Validator;

/**
 * Representa uma Especialidade. Deve ter uma lista das consultas marcadas e uma
 * lista com as senhas a serem atendidas no dia atual.
 */
public class Especialidade {

	// para simplificar, cada serviço vai ter sempre o mesmo horário
	private HorarioDiario horario = new HorarioDiario(LocalTime.of(8, 10), LocalTime.of(19, 50));

	private String id;
	private String descricao;
	private String sala;
	private List<Consulta> consultas;  // ordenada cronologicamente
	private List<Senha> emEspera;      // ordenada pela hora prevista de atendimento

	public Especialidade(String id, String descricao, String sala) {
		this.id = Validator.requireNonBlankTrimmed(id);
		this.descricao = Validator.requireNonBlankTrimmed(descricao);
		this.sala = Validator.requireNonBlankTrimmed(sala);
		this.consultas = new ArrayList<>();
		this.emEspera = new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = Validator.requireNonBlankTrimmed(descricao);
	}

	public String getSala() {
		return sala;
	}

	public void setSala(String sala) {
		this.sala = Validator.requireNonBlankTrimmed(sala);
	}

	/**
	 * Adiciona uma consulta à lista da especialidade, mantendo a ordem cronológica.
	 *
	 * @param c a consulta a adicionar
	 */
	public void addConsulta(Consulta c) {
		int i = 0;
		while (i < consultas.size() && consultas.get(i).antecede(c))
			i++;
		consultas.add(i, c);
	}

	/**
	 * Remove uma consulta da lista da especialidade.
	 *
	 * @param c a consulta a remover
	 */
	public void removeConsulta(Consulta c) {
		consultas.remove(c);
	}

	/**
	 * Retorna a lista de consultas da especialidade.
	 *
	 * @return lista de consultas ordenada cronologicamente
	 */
	public List<Consulta> getConsultas() {
		return new ArrayList<>(consultas);
	}

	/**
	 * Adiciona uma senha à lista de espera da especialidade,
	 * ordenada pela hora prevista de atendimento.
	 *
	 * @param s a senha a adicionar
	 */
	public void addSenha(Senha s) {
		int i = 0;
		while (i < emEspera.size() && emEspera.get(i).getAtendimento().isBefore(s.getAtendimento()))
			i++;
		emEspera.add(i, s);
	}

	/**
	 * Retorna qual a próxima senha em espera
	 *
	 * @return a próxima senha em espera
	 */
	public Senha getProximaSenha() {
		if (emEspera.isEmpty())
			return null;
		return emEspera.get(0);
	}

	/**
	 * O utente não responde à chamada? Coloca a senha/consulta para 15 minutos mais
	 * tarde e passa ao próximo utente.
	 */
	public void rejeitaProximaSenha() {
		if (emEspera.isEmpty())
			return;
		Senha s = emEspera.remove(0);
		LocalTime novaHora = s.getAtendimento().toLocalTime().plusMinutes(15);
		if (!horario.contem(novaHora)) {
			// hora fora do horário — descarta a senha e anula a consulta
			s.getConsulta().getUtente().removeConsulta(s.getConsulta());
			s.getConsulta().getEspecialidade().removeConsulta(s.getConsulta());
			return;
		}
		s.setAtendimento(s.getAtendimento().plusMinutes(15));
		addSenha(s);
	}

	/**
	 * Terminou a consulta da senha
	 *
	 * @param s a senha cuja consulta terminou
	 */
	public void terminaConsulta(Senha s) {
		emEspera.remove(s);
		s.terminaAtendimento();
	}

	/**
	 * Retorna as senhas que estão em lista de espera para serem atendidas nesta
	 * especialidade
	 *
	 * @return as senhas que estão em lista de espera para serem atendidas
	 */
	public Collection<Senha> getEmEspera() {
		return new ArrayList<>(emEspera);
	}

	@Override
	public String toString() {
		return id + " - " + descricao;
	}
}