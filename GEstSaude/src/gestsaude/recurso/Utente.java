package gestsaude.recurso;

import java.util.ArrayList;
import java.util.List;

import poo.util.Validator;

/**
 * Representa um Utente. Deve ter um nome, o número de SNS e armazenar as
 * consultas que tem marcadas no futuro.
 */
public class Utente {

    private int sns;
    private String nome;
    private List<Consulta> consultas; // lista ordenada cronologicamente

    public Utente(int sns, String nome) {
        this.sns = sns;
        this.nome = Validator.requireNonBlankTrimmed(nome);
        this.consultas = new ArrayList<>();
    }

    public int getSns() {
        return sns;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = Validator.requireNonBlankTrimmed(nome);
    }

    /**
     * Adiciona uma consulta à lista do utente, mantendo a ordem cronológica.
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
     * Remove uma consulta da lista do utente.
     *
     * @param c a consulta a remover
     */
    public void removeConsulta(Consulta c) {
        consultas.remove(c);
    }

    /**
     * Retorna a lista de consultas do utente.
     *
     * @return lista de consultas ordenada cronologicamente
     */
    public List<Consulta> getConsultas() {
        return new ArrayList<>(consultas);
    }

    @Override
    public String toString() {
        return sns + " - " + nome;
    }
}