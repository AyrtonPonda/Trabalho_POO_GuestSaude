package gestsaude.recurso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import poo.util.Validator;

/**
 * Representa um serviço. Deve ter o id, uma descrição, a sala. Deve ter uma
 * lista das senhas que ainda terão de ser atendidas.
 */
public class Servico {

    private String id;
    private String descricao;
    private String sala;
    private List<Senha> emEspera; // ordenada pela ordem de encaminhamento

    public Servico(String id, String descricao, String sala) {
        this.id = Validator.requireNonBlankTrimmed(id);
        this.descricao = Validator.requireNonBlankTrimmed(descricao);
        this.sala = Validator.requireNonBlankTrimmed(sala);
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
     * Adiciona uma senha à lista de espera deste serviço,
     * pela ordem de encaminhamento (ordem de chegada).
     *
     * @param s a senha a adicionar
     */
    public void addSenha(Senha s) {
        emEspera.add(s);
    }

    /**
     * Retorna a próxima senha a ser atendida por este serviço.
     *
     * @return a próxima senha, null se não tiver mais senhas
     */
    public Senha getProximaSenha() {
        if (emEspera.isEmpty())
            return null;
        return emEspera.get(0);
    }

    /**
     * O utente não responde à chamada? A sua senha passa para o fim da lista.
     */
    public void saltaProximaSenha() {
        if (emEspera.isEmpty())
            return;
        Senha s = emEspera.remove(0);
        emEspera.add(s);
    }

    /**
     * A senha termina a consulta neste serviço.
     *
     * @param s a senha que terminou o serviço
     */
    public void terminaConsulta(Senha s) {
        emEspera.remove(s);
        s.terminaAtendimento();
    }

    /**
     * Retorna as senhas que estão em lista de espera para serem atendidas neste
     * serviço.
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