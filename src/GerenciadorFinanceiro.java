// Arquivo: GerenciadorFinanceiro.java

// Importações necessárias para JSON e Arquivos
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/*
 * Esta é a classe "Cérebro". Ela controla toda a lógica.
 */
public class GerenciadorFinanceiro {

    // A lista que guarda TODAS as transações na memória
    private List<Transacao> transacoes;
    private double saldo;

    // Ferramentas para o JSON
    private Gson gson;
    // Constante com o nome do arquivo que vamos usar
    private static final String NOME_ARQUIVO = "financas.json";

    // Construtor: O que acontece quando o programa inicia
    public GerenciadorFinanceiro() {
        // Configura o Gson para formatar o JSON de forma legível
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        // Tenta carregar os dados do arquivo
        carregarDados();

        // Se, após carregar, a lista não existir (arquivo vazio/novo)
        // cria uma lista nova em branco.
        if (this.transacoes == null) {
            this.transacoes = new ArrayList<>();
        }

        // Recalcula o saldo com base no que foi carregado
        recalcularSaldoTotal();
    }

    // --- Métodos de Persistência (Salvar/Carregar) ---

    // O "A+" (1): Salvar os dados no arquivo JSON
    private void salvarDados() {
        try (Writer writer = new FileWriter(NOME_ARQUIVO)) {
            // Converte a lista 'transacoes' para JSON e salva no arquivo
            gson.toJson(this.transacoes, writer);
        } catch (IOException e) {
            System.out.println("Erro ao salvar os dados: " + e.getMessage());
        }
    }

    // O "A+" (2): Carregar os dados do arquivo JSON
    private void carregarDados() {
        try (Reader reader = new FileReader(NOME_ARQUIVO)) {
            // Define o "tipo" da nossa lista (necessário para o Gson)
            Type tipoLista = new TypeToken<ArrayList<Transacao>>() {}.getType();

            // Converte o JSON do arquivo de volta para a lista 'transacoes'
            this.transacoes = gson.fromJson(reader, tipoLista);
            System.out.println("Dados carregados com sucesso!");

        } catch (FileNotFoundException e) {
            // Isso é normal se for a primeira vez rodando
            System.out.println("Arquivo de dados não encontrado. Um novo será criado.");
        } catch (IOException e) {
            System.out.println("Erro ao carregar os dados: " + e.getMessage());
        }
    }

    // --- Métodos de Lógica Financeira ---

    // Recalcula o saldo DO ZERO, lendo a lista inteira
    private void recalcularSaldoTotal() {
        this.saldo = 0.0;
        for (Transacao t : this.transacoes) {
            if (t.getTipo().equalsIgnoreCase("RECEITA")) {
                this.saldo += t.getValor();
            } else {
                this.saldo -= t.getValor();
            }
        }
    }

    // Adiciona uma nova transação
    public void adicionarTransacao(Transacao novaTransacao) {
        this.transacoes.add(novaTransacao);
        // Atualiza o saldo (mais rápido do que recalcular tudo)
        if (novaTransacao.getTipo().equalsIgnoreCase("RECEITA")) {
            this.saldo += novaTransacao.getValor();
        } else {
            this.saldo -= novaTransacao.getValor();
        }

        // SALVA AUTOMATICAMENTE
        salvarDados();
        System.out.println("Transação adicionada e dados salvos!");
    }

    // Apenas mostra o saldo atual
    public double getSaldo() {
        return this.saldo;
    }

    // Lista todas as transações
    public void listarTransacoes() {
        if (transacoes.isEmpty()) {
            System.out.println("Nenhuma transação registrada.");
            return;
        }

        System.out.println("\n--- Histórico de Transações ---");
        // Loop 'for' para mostrar com um índice (1, 2, 3...)
        for (int i = 0; i < transacoes.size(); i++) {
            // (i + 1) para começar a contagem do 1, não do 0
            System.out.printf("%d. %s\n", (i + 1), transacoes.get(i));
        }
    }

    // Remove uma transação PELO ÍNDICE (o número 1, 2, 3... da lista)
    public void removerTransacao(int indice) {
        // Validação: verifica se o índice é válido
        // (Lembre-se: o usuário digita 1, mas na lista é o índice 0)
        if (indice < 1 || indice > transacoes.size()) {
            System.out.println("Erro: Índice inválido!");
            return;
        }

        // Remove da lista
        // (indice - 1) para ajustar do "modo usuário" (1,2,3) para o "modo lista" (0,1,2)
        Transacao removida = transacoes.remove(indice - 1);

        System.out.println("Transação removida: " + removida);

        // Precisamos recalcular o saldo do zero, pois não sabemos
        // se era receita ou despesa.
        recalcularSaldoTotal();

        // Salva as mudanças no arquivo
        salvarDados();
    }

    // Retorna UMA transação (para editar)
    public Transacao getTransacaoPorIndice(int indice) {
        if (indice < 1 || indice > transacoes.size()) {
            return null; // Não achou
        }
        return transacoes.get(indice - 1);
    }

    // Método para salvar após editar
    // (Não precisamos de um método "editar", pois o Main vai
    // pegar o objeto, usar os "setters", e só precisamos salvar)
    public void salvarAposEdicao() {
        // Após editar um objeto, o saldo PODE ter mudado
        recalcularSaldoTotal();
        // E salvamos o estado atual da lista
        salvarDados();
        System.out.println("Edição salva com sucesso!");
    }

    // Arquivo: GerenciadorFinanceiro.java
// ... (cole isso dentro da classe)

    // O "A+" (3): Relatórios (Lógica de verdade!)
    public void gerarRelatorioPorCategoria() {
        System.out.println("\n--- Relatório de Gastos por Categoria ---");

        // 1. Precisamos de um Mapa para guardar: Categoria -> Valor Total
        // Ex: "Alimentação" -> 250.50
        // Ex: "Transporte" -> 120.00
        java.util.Map<String, Double> gastosPorCategoria = new java.util.HashMap<>();

        double totalDespesas = 0;

        // 2. Loop em TODAS as transações
        for (Transacao t : transacoes) {
            if (t.getTipo().equalsIgnoreCase("DESPESA")) {
                String categoria = t.getCategoria();
                double valor = t.getValor();

                totalDespesas += valor;

                // Adiciona ao mapa
                // 'getOrDefault' pega o valor atual (ou 0 se não existir)
                // e soma o novo valor.
                gastosPorCategoria.put(categoria,
                        gastosPorCategoria.getOrDefault(categoria, 0.0) + valor);
            }
        }

        if (totalDespesas == 0) {
            System.out.println("Nenhuma despesa registrada para gerar relatório.");
            return;
        }

        // 3. Mostra os resultados
        System.out.printf("Total de Despesas: R$ %.2f\n", totalDespesas);
        System.out.println("Gastos por Categoria:");

        for (String categoria : gastosPorCategoria.keySet()) {
            double gastoCategoria = gastosPorCategoria.get(categoria);
            double percentual = (gastoCategoria / totalDespesas) * 100.0;

            System.out.printf("  - %s: R$ %.2f (%.1f%%)\n",
                    categoria, gastoCategoria, percentual);
        }
    }
}