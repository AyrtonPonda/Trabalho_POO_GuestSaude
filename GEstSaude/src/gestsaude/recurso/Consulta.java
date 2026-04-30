package gestsaude.recurso;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Representa uma Consulta. Deve ter o dia e hora da marcação, o utente e a
 * especialidade.
 */
public class Consulta {

	private static final LocalTime HORA_INICIO = LocalTime.of(8, 10);
	private static final LocalTime HORA_FIM = LocalTime.of(19, 50);

	private Utente utente;
	private Especialidade especialidade;
	private LocalDateTime dataHora;

	public Consulta(Utente utente, Especialidade especialidade, LocalDateTime dataHora) {
		this.utente = Objects.requireNonNull(utente);
		this.especialidade = Objects.requireNonNull(especialidade);
		this.dataHora = dataHora;
	}

	public Utente getUtente() {
		return utente;
	}

	public Especialidade getEspecialidade() {
		return especialidade;
	}

	public LocalDateTime getDataHora() {
		return dataHora;
	}

	public void setDataHora(LocalDateTime dataHora) {
		if (dataHora != null)
			this.dataHora = dataHora;
	}

	/**
	 * Verifica se o horário da consulta está dentro do período permitido (8:10 - 19:50)
	 *
	 * @return true se o horário for válido
	 */
	private boolean horarioValido() {
		LocalTime hora = dataHora.toLocalTime();
		return hora.compareTo(HORA_INICIO) >= 0 && hora.compareTo(HORA_FIM) <= 0;
	}

	/**
	 * Indica se a consulta está validada, ou seja, se tem utente, especialidade,
	 * data/hora definidos e o horário é válido.
	 *
	 * @return true se a consulta está válida
	 */
	public boolean estaValidada() {
		return dataHora != null && horarioValido();
	}

	/**
	 * Indica se esta consulta antecede outra cronologicamente.
	 * Em caso de empate na data/hora, ordena alfabeticamente pelo id da especialidade.
	 *
	 * @param outra a consulta a comparar
	 * @return true se esta consulta for anterior à outra
	 */
	public boolean antecede(Consulta outra) {
		if (this.dataHora.equals(outra.dataHora))
			return this.especialidade.getId().compareTo(outra.especialidade.getId()) < 0;
		return this.dataHora.isBefore(outra.dataHora);
	}

	/**
	 * Representação textual da consulta para listagens.
	 *
	 * @return string com os dados da consulta
	 */
	@Override
	public String toString() {
		return utente.getNome() + " | " + especialidade.getDescricao()
				+ " | " + dataHora.toLocalDate() + " " + dataHora.toLocalTime();
	}
}