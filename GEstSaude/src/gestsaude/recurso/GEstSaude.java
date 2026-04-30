package gestsaude.recurso;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import poo.util.RelogioSimulado;

/**
 * Representa o sistema. Deve armazenar todas as consultas, utentes,
 * especialidades, serviços e senhas.
 */
public class GEstSaude {

	// constantes para erros e situações que podem ocorrer para usar no retorno de
	// alguns métodos
	public static final int CONSULTA_ACEITE = 0;
	public static final int CONSULTA_JA_VALIDADA = CONSULTA_ACEITE + 1;
	public static final int UTENTE_SEM_CONSULTA_HOJE = CONSULTA_JA_VALIDADA + 1;
	public static final int UTENTE_SEM_CONSULTA_PROXIMA = UTENTE_SEM_CONSULTA_HOJE + 1;
	public static final int UTENTE_DEMASIADO_ATRASADO = UTENTE_SEM_CONSULTA_PROXIMA + 1;
	public static final int UTENTE_ATRASADO_FORAHORAS = UTENTE_DEMASIADO_ATRASADO + 1;
	public static final int UTENTE_ATRASADO_ADIADO = UTENTE_ATRASADO_FORAHORAS + 1;
	public static final int SERVICO_SEM_CONSULTA = UTENTE_ATRASADO_ADIADO + 1;
	public static final int DATA_JA_PASSOU = SERVICO_SEM_CONSULTA + 1;
	public static final int UTENTE_JA_TEM_CONSULTA = DATA_JA_PASSOU + 1;
	public static final int ESPECIALISTA_JA_TEM_CONSULTA = UTENTE_JA_TEM_CONSULTA + 1;
	public static final int ALTERACAO_INVALIDA = ESPECIALISTA_JA_TEM_CONSULTA + 1;

	private static final int MINUTOS_INTERVALO_ESPECIALIDADE = 10;
	private static final int HORAS_INTERVALO_UTENTE = 3;
	private static final int HORAS_ATRASO_MAXIMO = 2;
	private static final int MINUTOS_ATRASO_ATENDIMENTO = 45;

	private List<Utente> utentes;
	private List<Consulta> consultas;   // ordenada cronologicamente
	private List<Especialidade> especialidades;
	private List<Servico> servicos;
	private List<Senha> senhas;

	public GEstSaude() {
		utentes = new ArrayList<>();
		consultas = new ArrayList<>();
		especialidades = new ArrayList<>();
		servicos = new ArrayList<>();
		senhas = new ArrayList<>();
	}

	// -------------------------------------------------------------------------
	// Gestão de utentes
	// -------------------------------------------------------------------------

	public void addUtente(Utente u) {
		utentes.add(u);
	}

	public Utente getUtente(int sns) {
		for (Utente u : utentes)
			if (u.getSns() == sns)
				return u;
		return null;
	}

	public Collection<Utente> getUtentes() {
		return new ArrayList<>(utentes);
	}

	// -------------------------------------------------------------------------
	// Gestão de especialidades
	// -------------------------------------------------------------------------

	public void addEspecialidade(Especialidade e) {
		especialidades.add(e);
	}

	public Especialidade getEspecialidade(String id) {
		for (Especialidade e : especialidades)
			if (e.getId().equals(id))
				return e;
		return null;
	}

	public Collection<Especialidade> getEspecialidades() {
		return new ArrayList<>(especialidades);
	}

	// -------------------------------------------------------------------------
	// Gestão de serviços
	// -------------------------------------------------------------------------

	public void addServico(Servico s) {
		servicos.add(s);
	}

	public Servico getServico(String id) {
		for (Servico s : servicos)
			if (s.getId().equals(id))
				return s;
		return null;
	}

	public Collection<Servico> getServicos() {
		return new ArrayList<>(servicos);
	}

	// -------------------------------------------------------------------------
	// Gestão de consultas
	// -------------------------------------------------------------------------

	/**
	 * Retorna todas as consultas do sistema ordenadas cronologicamente.
	 */
	public Collection<Consulta> getConsultas() {
		return new ArrayList<>(consultas);
	}

	/**
	 * Retorna todas as consultas do dia de hoje.
	 */
	public List<Consulta> getConsultasHoje() {
		LocalDate hoje = RelogioSimulado.getTempoAtual().toLocalDate();
		List<Consulta> resultado = new ArrayList<>();
		for (Consulta c : consultas)
			if (c.getDataHora().toLocalDate().equals(hoje))
				resultado.add(c);
		return resultado;
	}

	/**
	 * Retorna todas as consultas de um utente.
	 */
	public List<Consulta> getConsultasUtente(Utente u) {
		List<Consulta> resultado = new ArrayList<>();
		for (Consulta c : consultas)
			if (c.getUtente() == u)
				resultado.add(c);
		return resultado;
	}

	/**
	 * Retorna todas as consultas de uma especialidade.
	 */
	public List<Consulta> getConsultasEspecialidade(Especialidade e) {
		List<Consulta> resultado = new ArrayList<>();
		for (Consulta c : consultas)
			if (c.getEspecialidade() == e)
				resultado.add(c);
		return resultado;
	}

	/**
	 * Deve retornar qual a primeira consulta do dia de um utente.
	 *
	 * @param u o utente de quem ver a primeira consulta do dia
	 * @return a primeira consulta do dia do utente
	 */
	public Consulta primeiraConsultaDia(Utente u) {
		LocalDate hoje = RelogioSimulado.getTempoAtual().toLocalDate();
		for (Consulta c : consultas)
			if (c.getUtente() == u && c.getDataHora().toLocalDate().equals(hoje))
				return c;
		return null;
	}

	/**
	 * Verifica se a consulta pode ser adicionada ao sistema.
	 */
	public int podeAceitarConsulta(Consulta c) {
		LocalDateTime agora = RelogioSimulado.getTempoAtual();

		// data já passou
		if (c.getDataHora().isBefore(agora))
			return DATA_JA_PASSOU;

		// verificar conflito no utente (menos de 3 horas de diferença no mesmo dia)
		for (Consulta outra : consultas) {
			if (outra.getUtente() == c.getUtente()
					&& outra.getDataHora().toLocalDate().equals(c.getDataHora().toLocalDate())) {
				long minutos = minutosEntre(c.getDataHora(), outra.getDataHora());
				if (minutos < HORAS_INTERVALO_UTENTE * 60)
					return UTENTE_JA_TEM_CONSULTA;
			}
		}

		// verificar conflito na especialidade (menos de 10 minutos)
		for (Consulta outra : consultas) {
			if (outra.getEspecialidade() == c.getEspecialidade()) {
				long minutos = minutosEntre(c.getDataHora(), outra.getDataHora());
				if (minutos < MINUTOS_INTERVALO_ESPECIALIDADE)
					return ESPECIALISTA_JA_TEM_CONSULTA;
			}
		}

		return CONSULTA_ACEITE;
	}

	/**
	 * Verifica se a consulta pode ser alterada.
	 */
	public int podeAlterarConsulta(Consulta antiga, Consulta nova) {
		// não se pode mudar o utente nem a especialidade
		if (antiga.getUtente() != nova.getUtente()
				|| antiga.getEspecialidade() != nova.getEspecialidade())
			return ALTERACAO_INVALIDA;

		LocalDateTime agora = RelogioSimulado.getTempoAtual();

		// data já passou
		if (nova.getDataHora().isBefore(agora))
			return DATA_JA_PASSOU;

		// verificar conflito no utente (ignorando a própria consulta)
		for (Consulta outra : consultas) {
			if (outra == antiga)
				continue;
			if (outra.getUtente() == nova.getUtente()
					&& outra.getDataHora().toLocalDate().equals(nova.getDataHora().toLocalDate())) {
				long minutos = minutosEntre(nova.getDataHora(), outra.getDataHora());
				if (minutos < HORAS_INTERVALO_UTENTE * 60)
					return UTENTE_JA_TEM_CONSULTA;
			}
		}

		// verificar conflito na especialidade (ignorando a própria consulta)
		for (Consulta outra : consultas) {
			if (outra == antiga)
				continue;
			if (outra.getEspecialidade() == nova.getEspecialidade()) {
				long minutos = minutosEntre(nova.getDataHora(), outra.getDataHora());
				if (minutos < MINUTOS_INTERVALO_ESPECIALIDADE)
					return ESPECIALISTA_JA_TEM_CONSULTA;
			}
		}

		return CONSULTA_ACEITE;
	}

	/**
	 * Adiciona uma consulta ao sistema.
	 */
	public int aceitaConsulta(Consulta c) {
		int res = podeAceitarConsulta(c);
		if (res != CONSULTA_ACEITE)
			return res;
		// inserir ordenada cronologicamente
		int i = 0;
		while (i < consultas.size() && consultas.get(i).antecede(c))
			i++;
		consultas.add(i, c);
		c.getUtente().addConsulta(c);
		c.getEspecialidade().addConsulta(c);
		return CONSULTA_ACEITE;
	}

	/**
	 * Altera uma consulta por outra.
	 */
	public int alteraConsulta(Consulta antiga, Consulta nova) {
		int res = podeAlterarConsulta(antiga, nova);
		if (res != CONSULTA_ACEITE)
			return res;
		// remover a antiga
		consultas.remove(antiga);
		antiga.getUtente().removeConsulta(antiga);
		antiga.getEspecialidade().removeConsulta(antiga);
		// adicionar a nova
		int i = 0;
		while (i < consultas.size() && consultas.get(i).antecede(nova))
			i++;
		consultas.add(i, nova);
		nova.getUtente().addConsulta(nova);
		nova.getEspecialidade().addConsulta(nova);
		return CONSULTA_ACEITE;
	}

	/**
	 * Remove uma consulta do sistema.
	 */
	public void removeConsulta(Consulta c) {
		consultas.remove(c);
		c.getUtente().removeConsulta(c);
		c.getEspecialidade().removeConsulta(c);
	}

	/**
	 * Indicação ao sistema de que a consulta terminou.
	 * Elimina a consulta e a senha respetiva.
	 */
	public void terminaConsulta(Consulta c) {
		consultas.remove(c);
		c.getUtente().removeConsulta(c);
		c.getEspecialidade().removeConsulta(c);
		// remover senha associada
		Senha s = getSenha(c);
		if (s != null)
			senhas.remove(s);
	}

	// -------------------------------------------------------------------------
	// Gestão de senhas
	// -------------------------------------------------------------------------

	/**
	 * Valida a consulta. Verifica se a consulta pode ser concretizada e,
	 * se sim, emite a senha respetiva.
	 */
	public int validarConsulta(Consulta c) {
		LocalDateTime agora = RelogioSimulado.getTempoAtual();
		LocalDateTime dataConsulta = c.getDataHora();


		if (getSenha(c) != null)
			return CONSULTA_JA_VALIDADA;

		long minutos = minutosEntre(agora, dataConsulta);
		boolean atrasado = agora.isAfter(dataConsulta);


		if (atrasado && minutos > HORAS_ATRASO_MAXIMO * 60) {
			removeConsulta(c);
			return UTENTE_DEMASIADO_ATRASADO;
		}


		if (!atrasado && minutos > HORAS_INTERVALO_UTENTE * 60)
			return UTENTE_SEM_CONSULTA_PROXIMA;

		LocalDateTime entrada = agora;
		LocalDateTime atendimento;

		if (atrasado) {

			atendimento = entrada.plusMinutes(MINUTOS_ATRASO_ATENDIMENTO);
			LocalTime fimHorario = LocalTime.of(19, 50);
			if (atendimento.toLocalTime().isAfter(fimHorario)) {
				removeConsulta(c);
				return UTENTE_ATRASADO_FORAHORAS;
			}
			emiteSenha(c, entrada, atendimento);
			return UTENTE_ATRASADO_ADIADO;
		}

		atendimento = dataConsulta;
		emiteSenha(c, entrada, atendimento);
		return CONSULTA_ACEITE;
	}

	/**
	 * Emite a senha para a consulta.
	 */
	public Senha emiteSenha(Consulta c, LocalDateTime entrada, LocalDateTime atendimento) {
		Senha s = getSenha(c);
		if (s != null)
			return s;
		s = new Senha(this, c, entrada, atendimento);
		senhas.add(s);
		c.getEspecialidade().addSenha(s);
		return s;
	}

	/**
	 * Retorna a senha associada a uma consulta, ou null se não existir.
	 */
	public Senha getSenha(Consulta c) {
		for (Senha s : senhas)
			if (s.getConsulta() == c)
				return s;
		return null;
	}

	/**
	 * Retorna todas as senhas do sistema.
	 */
	public Collection<Senha> getSenhas() {
		return new ArrayList<>(senhas);
	}

	// -------------------------------------------------------------------------
	// Utilitários
	// -------------------------------------------------------------------------

	/**
	 * Calcula a diferença em minutos (valor absoluto) entre dois LocalDateTime.
	 */
	private long minutosEntre(LocalDateTime a, LocalDateTime b) {
		LocalDateTime menor = a.isBefore(b) ? a : b;
		LocalDateTime maior = a.isBefore(b) ? b : a;
		long minutos = 0;
		while (menor.plusMinutes(60).compareTo(maior) <= 0) {
			menor = menor.plusMinutes(60);
			minutos += 60;
		}
		while (menor.plusMinutes(1).compareTo(maior) <= 0) {
			menor = menor.plusMinutes(1);
			minutos++;
		}
		return minutos;
	}
}