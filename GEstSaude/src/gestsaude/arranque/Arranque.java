package gestsaude.arranque;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

import javax.swing.Timer;

import gestsaude.menu.MaquinaEntrada;
import gestsaude.menu.MenuEspecialidade;
import gestsaude.menu.MenuSecretaria;
import gestsaude.menu.MenuServico;
import gestsaude.menu.RelogioDialog;
import gestsaude.recurso.*;
import poo.util.RelogioSimulado;

/**
 * Classe responsavel pelo arranque do sistema.
 * Tem um método para criar a configuração de teste
 */
public class Arranque {

	/**
	 * Cria a configuração inicial do sistema
	 * 
	 * @return um GEstSaude já completamente configurado
	 */
	public static GEstSaude getGEstSaude() {
		// colocar o relógio simulado nas 8:00
		RelogioSimulado.getRelogioSimulado().setTempoAtual(LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0)));

		GEstSaude g = new GEstSaude();

		// TODO criar os utentes

		// TODO criar as especialidades

		// TODO criar os serviços

		// TODO criar as consultas
		// --- Utentes ---
		g.addUtente(new Utente(121, "Raquel Marques Soares"));
		g.addUtente(new Utente(122, "Daniel Mendes Rodrigues"));
		g.addUtente(new Utente(123, "Zeferino Dias Torres"));
		g.addUtente(new Utente(124, "Anabela Dias Santos"));
		g.addUtente(new Utente(125, "Felizberto Desgraçado"));
		g.addUtente(new Utente(126, "Antonina Martins Pires"));
		g.addUtente(new Utente(127, "Camaleão das Neves Freitas"));
		g.addUtente(new Utente(128, "João Pais Pereira"));
		g.addUtente(new Utente(129, "Carlos Freitas Lobo"));
		g.addUtente(new Utente(130, "Daniel Mendes Rodrigues"));
		g.addUtente(new Utente(120, "Dália Ribeiro Sanches"));

		// --- Especialidades ---
		g.addEspecialidade(new Especialidade("Oto1",  "Otorrino - Dr Narize",          "Gab. 22"));
		g.addEspecialidade(new Especialidade("Ofta1", "Oftalmologia - Dra Íris Tapada", "Gab. 7"));
		g.addEspecialidade(new Especialidade("Ped2",  "Pediatria - Dr B. B. Zinho",     "Gab. 2"));
		g.addEspecialidade(new Especialidade("Ped1",  "Pediatria - Dra P. Quena",       "Gab. 1"));
		g.addEspecialidade(new Especialidade("Derm1", "Dermatologia - Dra V. Ruga",     "Gab. 11"));
		g.addEspecialidade(new Especialidade("Pneu1", "Pneumologia - Dr Paul Mão",      "Gab. 12"));
		g.addEspecialidade(new Especialidade("Orto2", "Ortopedia - Dr Entorse",         "Gab. 10"));
		g.addEspecialidade(new Especialidade("Card1", "Cardiologia - Dr Paul Sassão",   "Gab. 4"));
		g.addEspecialidade(new Especialidade("Orto1", "Ortopedia - Dr Ossos",           "Gab. 9"));
		g.addEspecialidade(new Especialidade("Oto2",  "Otorrino - Dra Odete Otite",     "Gab. 23"));

		// --- Serviços ---
		g.addServico(new Servico("Scopia", "Lab. Endos",    "Endos/Colonoscopia"));
		g.addServico(new Servico("NeuLab", "Lab. Neurologia", "EEG + Dopler"));
		g.addServico(new Servico("Rad",    "Sala RX",       "Radiologia"));
		g.addServico(new Servico("Audio",  "Lab. Som",      "Audiometria"));
		g.addServico(new Servico("Enf",    "Enfermaria",    "Enfermaria"));

		// --- Consultas (hoje e amanhã) ---
		LocalDate hoje = LocalDate.now();
		LocalDate amanha = hoje.plusDays(1);

		g.aceitaConsulta(new Consulta(g.getUtente(122), g.getEspecialidade("Orto1"), LocalDateTime.of(hoje,   LocalTime.of(8, 10))));
		g.aceitaConsulta(new Consulta(g.getUtente(120), g.getEspecialidade("Ped1"),  LocalDateTime.of(hoje,   LocalTime.of(8, 10))));
		g.aceitaConsulta(new Consulta(g.getUtente(121), g.getEspecialidade("Ped2"),  LocalDateTime.of(hoje,   LocalTime.of(8, 10))));
		g.aceitaConsulta(new Consulta(g.getUtente(125), g.getEspecialidade("Derm1"), LocalDateTime.of(hoje,   LocalTime.of(8, 20))));
		g.aceitaConsulta(new Consulta(g.getUtente(126), g.getEspecialidade("Ped1"),  LocalDateTime.of(hoje,   LocalTime.of(8, 30))));
		g.aceitaConsulta(new Consulta(g.getUtente(127), g.getEspecialidade("Ped1"),  LocalDateTime.of(hoje,   LocalTime.of(8, 40))));
		g.aceitaConsulta(new Consulta(g.getUtente(127), g.getEspecialidade("Ped1"),  LocalDateTime.of(amanha, LocalTime.of(8, 10))));
		g.aceitaConsulta(new Consulta(g.getUtente(123), g.getEspecialidade("Ped1"),  LocalDateTime.of(amanha, LocalTime.of(8, 20))));

		return g;
	}

	/**
	 * arranque do sistema
	 */
	public static void main(String[] args) {
		// criar o GEstSaude
		GEstSaude gs = getGEstSaude();

		// Definir o tempo por segundo no relógio simulado
		RelogioSimulado relogio = RelogioSimulado.getRelogioSimulado();
		relogio.setTicksPorSegundo(1); // alterar este valor se quiserem que o tempo passe mais rápido

		// criar o menu da secretaria, neste caso irá ter apenas um
		MenuSecretaria secretaria = new MenuSecretaria(new Point(230, 10), "Secretaria", gs);
		secretaria.setVisible(true);

		// criar a janela do relógio
		RelogioDialog relogioDlg = new RelogioDialog(secretaria, new Point(10, 10));
		relogioDlg.setVisible(true);

		// criar uma máquina de entrada
		MaquinaEntrada me1 = new MaquinaEntrada(secretaria, new Point(10, 150), "Entrada 1", gs);
		me1.setVisible(true);

		// criar todos os menus de serviço e de especialidades
		// TODO ver as especialidades e os serviços do sistema
		Collection<Especialidade> especiais = gs.getEspecialidades();
		Collection<Servico> servicos = gs.getServicos();
		int idx = 0;
		int totalJanelas = 0;
		MenuEspecialidade menusEsp[] = new MenuEspecialidade[especiais.size()];
		for (Especialidade e : especiais) {
			menusEsp[idx] = new MenuEspecialidade(secretaria,
					new Point(10 + (totalJanelas % 5) * 200, 270 + (totalJanelas / 5) * 180), e, gs);
			menusEsp[idx].setVisible(true);
			idx++;
			totalJanelas++;
		}
		MenuServico menusServ[] = new MenuServico[servicos.size()];
		idx = 0;
		for (Servico s : servicos) {
			menusServ[idx] = new MenuServico(secretaria,
					new Point(10 + (totalJanelas % 5) * 200, 270 + (totalJanelas / 5) * 180), s);
			menusServ[idx].setVisible(true);
			idx++;
			totalJanelas++;
		}

		// criar um temporizador que vai atualizando as várias janelas
		// do menu de serviços, de 5 em 5 segundos (5000 milisegundos)
		Timer t = new Timer(5000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (MenuEspecialidade me : menusEsp)
					me.atualizarInfo();
				for (MenuServico ms : menusServ)
					ms.atualizarInfo();

				secretaria.atualizar();
			}
		});
		t.start();
	}
}
