package logica;// Arquivo: logica.GerenciadorFinanceiro.java (Corrigido)

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
// Import para checagem de JSON nulo
import com.google.gson.stream.JsonToken;

import modelo.Transacao;
import modelo.TransacaoRecorrente;
import modelo.Configuracao;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.YearMonth;
// CORREÇÃO: Padrão "aaaa" mudado para "yyyy"
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class GerenciadorFinanceiro {

    // --- Listas de Dados ---
    private List<Transacao> transacoes;
    private List<TransacaoRecorrente> transacoesRecorrentes;
    private Configuracao config;
    private double saldo;

    // --- Ferramentas ---
    private Gson gson;
    // CORREÇÃO: Padrão de data "aaaa" corrigido para "yyyy"
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // --- Constantes de Arquivos ---
    private static final String ARQUIVO_TRANSACOES = "financas.json";
    private static final String ARQUIVO_CONFIG = "configuracao.json";
    private static final String ARQUIVO_RECORRENTES = "recorrentes.json";

    // Construtor: O que acontece quando o programa inicia
    public GerenciadorFinanceiro() {
        // Configura o Gson com o adapter para LocalDate
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        // Carrega todas as fontes de dados
        carregarConfiguracao();
        carregarDados(); // Isso agora também recalcula o saldo
        carregarRecorrentes();

        // Se, após carregar, as listas não existirem, cria listas vazias.
        if (this.transacoes == null) {
            this.transacoes = new ArrayList<>();
        }
        if (this.transacoesRecorrentes == null) {
            this.transacoesRecorrentes = new ArrayList<>();
        }
        if (this.config == null) {
            this.config = new Configuracao();
        }
    }

    // --- Métodos de Persistência (Salvar/Carregar) ---

    // Salva as transações principais
    private void salvarDados() {
        try (Writer writer = new FileWriter(ARQUIVO_TRANSACOES)) {
            gson.toJson(this.transacoes, writer);
        } catch (IOException e) {
            System.out.println("Erro ao salvar transações: " + e.getMessage());
        }
    }

    // Carrega as transações principais
    private void carregarDados() {
        try (Reader reader = new FileReader(ARQUIVO_TRANSACOES)) {
            Type tipoLista = new TypeToken<ArrayList<Transacao>>() {
            }.getType();
            this.transacoes = gson.fromJson(reader, tipoLista);
            System.out.println("Transações carregadas com sucesso!");
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo de transações não encontrado. Um novo será criado.");
            this.transacoes = new ArrayList<>();
        } catch (IOException e) {
            System.out.println("Erro ao carregar transações: " + e.getMessage());
        }
        // Sempre recalcula o saldo após carregar
        recalcularSaldoTotal();
    }

    // Salva as configurações (senha, orçamento)
    private void salvarConfiguracao() {
        try (Writer writer = new FileWriter(ARQUIVO_CONFIG)) {
            gson.toJson(this.config, writer);
        } catch (IOException e) {
            System.out.println("Erro ao salvar configurações: " + e.getMessage());
        }
    }

    // Carrega as configurações
    private void carregarConfiguracao() {
        try (Reader reader = new FileReader(ARQUIVO_CONFIG)) {
            this.config = gson.fromJson(reader, Configuracao.class);
            if (this.config == null) {
                this.config = new Configuracao();
            }
            System.out.println("Configurações carregadas!");
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo de configuração não encontrado. Um novo será criado.");
            this.config = new Configuracao();
            // CORREÇÃO: Captura Exception genérica para pegar o JsonSyntaxException
            // (arquivo corrompido)
        } catch (Exception e) {
            System.out.println("Erro ao carregar configurações (arquivo pode estar corrompido): " + e.getMessage());
            System.out.println("Criando novo arquivo de configuração.");
            this.config = new Configuracao();
        }
    }

    // Salva as transações recorrentes
    private void salvarRecorrentes() {
        try (Writer writer = new FileWriter(ARQUIVO_RECORRENTES)) {
            gson.toJson(this.transacoesRecorrentes, writer);
        } catch (IOException e) {
            System.out.println("Erro ao salvar transações recorrentes: " + e.getMessage());
        }
    }

    // Carrega as transações recorrentes
    private void carregarRecorrentes() {
        try (Reader reader = new FileReader(ARQUIVO_RECORRENTES)) {
            Type tipoLista = new TypeToken<ArrayList<TransacaoRecorrente>>() {
            }.getType();
            this.transacoesRecorrentes = gson.fromJson(reader, tipoLista);
            System.out.println("Transações recorrentes carregadas!");
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo de recorrentes não encontrado. Um novo será criado.");
            this.transacoesRecorrentes = new ArrayList<>();
        } catch (IOException e) {
            System.out.println("Erro ao carregar recorrentes: " + e.getMessage());
            this.transacoesRecorrentes = new ArrayList<>();
        }
    }

    // --- Lógica Financeira Principal ---

    private void recalcularSaldoTotal() {
        this.saldo = 0.0;
        if (this.transacoes == null)
            return; // Checagem de segurança
        for (Transacao t : this.transacoes) {
            if (t.getTipo().equalsIgnoreCase("RECEITA")) {
                this.saldo += t.getValor();
            } else {
                this.saldo -= t.getValor();
            }
        }
    }

    public void adicionarTransacao(Transacao novaTransacao) {
        this.transacoes.add(novaTransacao);
        // Atualiza o saldo
        if (novaTransacao.getTipo().equalsIgnoreCase("RECEITA")) {
            this.saldo += novaTransacao.getValor();
        } else {
            this.saldo -= novaTransacao.getValor();
        }
        // Salva
        salvarDados();
        System.out.println("Transação adicionada e dados salvos!");
    }

    public double getSaldo() {
        return this.saldo;
    }

    // Feature 12: Histórico Detalhado (Extrato)
    public void listarTransacoes() {
        if (transacoes.isEmpty()) {
            System.out.println("Nenhuma transação registrada.");
            return;
        }
        System.out.println("\n--- Histórico de Transações (Extrato) ---");
        // Itera de trás para frente para mostrar as mais recentes primeiro
        for (int i = transacoes.size() - 1; i >= 0; i--) {
            System.out.printf("%d. %s\n", (i + 1), transacoes.get(i));
        }
    }

    public void removerTransacao(int indice) {
        if (indice < 1 || indice > transacoes.size()) {
            System.out.println("Erro: Índice inválido!");
            return;
        }
        Transacao removida = transacoes.remove(indice - 1);
        System.out.println("Transação removida: " + removida);
        // Recalcula o saldo e salva
        recalcularSaldoTotal();
        salvarDados();
    }

    public Transacao getTransacaoPorIndice(int indice) {
        if (indice < 1 || indice > transacoes.size()) {
            return null;
        }
        return transacoes.get(indice - 1);
    }

    public void salvarAposEdicao() {
        recalcularSaldoTotal();
        salvarDados();
        System.out.println("Edição salva com sucesso!");
    }

    // --- Feature 9: Login com Senha ---

    public boolean isPrimeiroLogin() {
        return this.config.getSenhaHash() == null || this.config.getSenhaHash().isEmpty();
    }

    public void definirNovaSenha(String novaSenha) {
        this.config.setSenhaHash(hashSenha(novaSenha));
        salvarConfiguracao();
        System.out.println("Senha definida com sucesso!");
    }

    public boolean validarLogin(String senhaDigitada) {
        String hashDigitado = hashSenha(senhaDigitada);
        return hashDigitado.equals(this.config.getSenhaHash());
    }

    // Usa SHA-256 para criar um hash da senha
    private String hashSenha(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(senha.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Erro crítico ao processar senha: " + e.getMessage());
            return senha; // Fallback inseguro, mas evita crash
        }
    }

    // --- Feature 1: Sistema de Orçamento Mensal ---

    public void setOrcamentoMensal(double orcamento) {
        this.config.setOrcamentoMensal(orcamento);
        salvarConfiguracao();
        System.out.printf("Orçamento mensal definido para R$ %.2f\n", orcamento);
    }

    public double getOrcamentoMensal() {
        return this.config.getOrcamentoMensal();
    }

    // Helper: Retorna os gastos de um mês específico
    private double getGastosMes(YearMonth mes) {
        double gastos = 0;
        for (Transacao t : transacoes) {
            if (t.getTipo().equalsIgnoreCase("DESPESA")) {
                try {
                    LocalDate data = LocalDate.parse(t.getData(), DATE_FORMATTER);
                    if (YearMonth.from(data).equals(mes)) {
                        gastos += t.getValor();
                    }
                } catch (DateTimeParseException e) {
                    // Ignora transações com data mal formatada
                }
            }
        }
        return gastos;
    }

    public double getGastosMesAtual() {
        return getGastosMes(YearMonth.now());
    }

    // Helper: Retorna as receitas de um mês específico
    private double getReceitasMes(YearMonth mes) {
        double receitas = 0;
        for (Transacao t : transacoes) {
            if (t.getTipo().equalsIgnoreCase("RECEITA")) {
                try {
                    LocalDate data = LocalDate.parse(t.getData(), DATE_FORMATTER);
                    if (YearMonth.from(data).equals(mes)) {
                        receitas += t.getValor();
                    }
                } catch (DateTimeParseException e) {
                    // Ignora
                }
            }
        }
        return receitas;
    }

    public double getReceitasMesAtual() {
        return getReceitasMes(YearMonth.now());
    }

    // --- Feature 2: Alertas Financeiros Automáticos ---

    public List<String> getAlertasFinanceiros() {
        List<String> alertas = new ArrayList<>();
        YearMonth mesAtual = YearMonth.now();
        double orcamento = getOrcamentoMensal();
        double gastosMes = getGastosMesAtual();

        // Alerta de Saldo Negativo
        if (this.saldo < 0) {
            alertas.add(String.format("ALERTA: Seu saldo está negativo! (R$ %.2f)", this.saldo));
        }

        // Alertas de Orçamento (Feature 1)
        if (orcamento > 0) {
            double percentualGasto = (gastosMes / orcamento) * 100.0;
            if (percentualGasto > 100.0) {
                alertas.add(String.format("ALERTA: Você ultrapassou seu orçamento de R$ %.2f! (Gastou R$ %.2f)",
                        orcamento, gastosMes));
            } else if (percentualGasto >= 80.0) {
                alertas.add(
                        String.format("ATENÇÃO: Você já utilizou %.0f%% do seu orçamento mensal (R$ %.2f de R$ %.2f)",
                                percentualGasto, gastosMes, orcamento));
            }
        }

        // Alerta de Gastos vs Receitas
        double receitasMes = getReceitasMesAtual();
        if (gastosMes > receitasMes) {
            alertas.add(String.format("ATENÇÃO: Você gastou (R$ %.2f) mais do que ganhou (R$ %.2f) este mês.",
                    gastosMes, receitasMes));
        }

        return alertas;
    }

    // --- Feature 3: Relatórios Estatísticos ---

    public String relatorioGastosPorCategoria() {
        Map<String, Double> gastosPorCategoria = new HashMap<>();
        double totalDespesas = 0;

        for (Transacao t : transacoes) {
            if (t.getTipo().equalsIgnoreCase("DESPESA")) {
                totalDespesas += t.getValor();
                gastosPorCategoria.put(t.getCategoria(),
                        gastosPorCategoria.getOrDefault(t.getCategoria(), 0.0) + t.getValor());
            }
        }

        if (totalDespesas == 0) {
            return "Nenhuma despesa registrada para gerar relatório.";
        }

        StringBuilder relatorio = new StringBuilder();
        relatorio.append(String.format("Total de Despesas: R$ %.2f\n", totalDespesas));
        relatorio.append("Gastos por Categoria:\n");

        for (Map.Entry<String, Double> entry : gastosPorCategoria.entrySet()) {
            double percentual = (entry.getValue() / totalDespesas) * 100.0;
            relatorio.append(String.format("  - %s: R$ %.2f (%.1f%%)\n",
                    entry.getKey(), entry.getValue(), percentual));
        }
        return relatorio.toString();
    }

    public String relatorioEntradasVsSaidasMesAtual() {
        double gastos = getGastosMesAtual();
        double receitas = getReceitasMesAtual();
        double balanco = receitas - gastos;
        return String.format("--- Balanço do Mês (%s) ---\n", YearMonth.now()) +
                String.format("Total de Receitas:   R$ %.2f\n", receitas) +
                String.format("Total de Despesas: - R$ %.2f\n", gastos) +
                String.format("Balanço Mensal:      R$ %.2f\n", balanco);
    }

    public String relatorioMaiorDespesaMesAtual() {
        Transacao maiorDespesa = null;
        YearMonth mesAtual = YearMonth.now();

        for (Transacao t : transacoes) {
            if (t.getTipo().equalsIgnoreCase("DESPESA")) {
                try {
                    if (YearMonth.from(LocalDate.parse(t.getData(), DATE_FORMATTER)).equals(mesAtual)) {
                        if (maiorDespesa == null || t.getValor() > maiorDespesa.getValor()) {
                            maiorDespesa = t;
                        }
                    }
                } catch (DateTimeParseException e) {
                }
            }
        }
        if (maiorDespesa == null) {
            return "Nenhuma despesa registrada este mês.";
        }
        return "Maior Despesa do Mês: " + maiorDespesa.toString();
    }

    public String relatorioMediaGastosDiariosMesAtual() {
        double gastos = getGastosMesAtual();
        int diasDoMes = LocalDate.now().getDayOfMonth();
        double media = (diasDoMes == 0) ? gastos : (gastos / diasDoMes); // Evita divisão por zero
        return String.format("Média de gastos diários este mês: R$ %.2f/dia", media);
    }

    // --- Feature 6: Busca de Transações ---

    public List<Transacao> buscarTransacoes(String dataInicioStr, String dataFimStr, String categoria) {
        LocalDate dataInicio = null;
        LocalDate dataFim = null;

        try {
            if (dataInicioStr != null && !dataInicioStr.isEmpty()) {
                dataInicio = LocalDate.parse(dataInicioStr, DATE_FORMATTER);
            }
            if (dataFimStr != null && !dataFimStr.isEmpty()) {
                dataFim = LocalDate.parse(dataFimStr, DATE_FORMATTER);
            }
        } catch (DateTimeParseException e) {
            System.out.println("Formato de data inválido. Use dd/MM/yyyy.");
            return new ArrayList<>(); // Retorna lista vazia
        }

        LocalDate finalDataInicio = dataInicio;
        LocalDate finalDataFim = dataFim;

        return transacoes.stream()
                // Filtra por Categoria
                .filter(t -> categoria == null || categoria.isEmpty() || t.getCategoria().equalsIgnoreCase(categoria))
                // Filtra por Data de Início
                .filter(t -> {
                    if (finalDataInicio == null)
                        return true;
                    try {
                        return !LocalDate.parse(t.getData(), DATE_FORMATTER).isBefore(finalDataInicio);
                    } catch (DateTimeParseException e) {
                        return false;
                    }
                })
                // Filtra por Data de Fim
                .filter(t -> {
                    if (finalDataFim == null)
                        return true;
                    try {
                        return !LocalDate.parse(t.getData(), DATE_FORMATTER).isAfter(finalDataFim);
                    } catch (DateTimeParseException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    // --- Feature 7: Backup e Restauração ---

    public String backupDados() {
        String dataBackup = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String nomeBackup = String.format("financas_backup_%s.json", dataBackup);
        Path origem = Paths.get(ARQUIVO_TRANSACOES);
        Path destino = Paths.get(nomeBackup);
        try {
            if (!Files.exists(origem)) {
                return "Erro: Arquivo de finanças '" + ARQUIVO_TRANSACOES + "' não encontrado para backup.";
            }
            Files.copy(origem, destino, StandardCopyOption.REPLACE_EXISTING);
            return "Backup criado com sucesso: " + nomeBackup;
        } catch (IOException e) {
            return "Erro ao criar backup: " + e.getMessage();
        }
    }

    public List<String> listarBackups() {
        try {
            return Files.list(Paths.get(".")) // Lista arquivos no diretório atual
                    .map(Path::getFileName)
                    .map(String::valueOf)
                    .filter(nome -> nome.startsWith("financas_backup_") && nome.endsWith(".json"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Erro ao listar backups: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public String restaurarDados(String nomeBackup) {
        Path origem = Paths.get(nomeBackup);
        Path destino = Paths.get(ARQUIVO_TRANSACOES);
        try {
            Files.copy(origem, destino, StandardCopyOption.REPLACE_EXISTING);
            carregarDados(); // Recarrega os dados e recalcula o saldo
            return "Dados restaurados com sucesso do arquivo: " + nomeBackup;
        } catch (IOException e) {
            return "Erro ao restaurar dados: " + e.getMessage();
        }
    }

    // --- Feature 8: Sugestões Inteligentes ---

    public List<String> getSugestoesInteligentes() {
        List<String> sugestoes = new ArrayList<>();
        YearMonth mesAtual = YearMonth.now();
        YearMonth mesAnterior = mesAtual.minusMonths(1);

        double gastosMesAtual = getGastosMes(mesAtual);
        double gastosMesAnterior = getGastosMes(mesAnterior);

        if (gastosMesAnterior > 0 && gastosMesAtual > gastosMesAnterior * 1.2) { // Aumento de 20%
            double percentual = ((gastosMesAtual / gastosMesAnterior) - 1) * 100;
            sugestoes.add(String.format(
                    "SUGESTÃO: Seus gastos este mês (R$ %.2f) estão %.0f%% maiores que no mês anterior (R$ %.2f). Revise suas despesas!",
                    gastosMesAtual, percentual, gastosMesAnterior));
        }

        // Outras sugestões podem ser adicionadas aqui...
        if (sugestoes.isEmpty()) {
            sugestoes.add("Nenhuma sugestão no momento. Continue gerenciando bem!");
        }
        return sugestoes;
    }

    // --- Feature 11: Transações Recorrentes ---

    public List<TransacaoRecorrente> getTransacoesRecorrentes() {
        return this.transacoesRecorrentes;
    }

    public void adicionarTransacaoRecorrente(TransacaoRecorrente tr) {
        this.transacoesRecorrentes.add(tr);
        salvarRecorrentes();
        System.out.println("Transação recorrente adicionada!");
    }

    /**
     * Retorna a lista completa de transações para a interface gráfica.
     */
    public List<Transacao> getListaDeTransacoes() {
        // Retorna a lista de transações.
        // Se a lista estiver vazia, retorna uma lista vazia em vez de null.
        if (this.transacoes == null) {
            return new ArrayList<>();
        }
        return this.transacoes;
    }

    // Este método deve ser chamado pela Interface após o login
    public void processarTransacoesRecorrentes() {
        LocalDate hoje = LocalDate.now();
        LocalDate dataUltimaVerificacao = config.getDataUltimaVerificacaoRecorrente();

        // Se nunca verificou OU se a última verificação foi ANTES de hoje
        if (dataUltimaVerificacao == null || dataUltimaVerificacao.isBefore(hoje)) {

            // Lógica simples: se hoje for um novo mês, processa
            boolean novoMes = (dataUltimaVerificacao == null || dataUltimaVerificacao.getMonth() != hoje.getMonth());

            if (novoMes) {
                System.out.println("Processando transações recorrentes para " + hoje.getMonth() + "...");
                int adicionadas = 0;
                for (TransacaoRecorrente tr : transacoesRecorrentes) {
                    // Verifica se o dia é válido para o mês (ex: dia 30 em Fev)
                    int dia = Math.min(tr.getDiaDoMes(), hoje.getMonth().length(hoje.isLeapYear()));

                    LocalDate dataTransacao = LocalDate.of(hoje.getYear(), hoje.getMonth(), dia);

                    // Cria uma transação normal baseada na recorrente
                    Transacao nova = new Transacao(
                            tr.getValor(),
                            tr.getTipo(),
                            tr.getCategoria(),
                            dataTransacao.format(DATE_FORMATTER), // Formata a data
                            tr.getDescricao() + " (Recorrente)");
                    adicionarTransacao(nova);
                    adicionadas++;
                }

                if (adicionadas > 0) {
                    System.out.println(adicionadas + " transações recorrentes foram adicionadas.");
                }
            }

            // Atualiza a data de verificação e salva
            config.setDataUltimaVerificacaoRecorrente(hoje);
            salvarConfiguracao();
        }
    }

    /*
     * Adapter customizado para GSON conseguir salvar e ler LocalDate.
     * Necessário para a Feature 11.
     */
    private static class LocalDateAdapter extends com.google.gson.TypeAdapter<LocalDate> {
        @Override
        public void write(com.google.gson.stream.JsonWriter out, LocalDate value) throws IOException {
            // CORREÇÃO: Adiciona checagem de nulo para evitar NullPointerException
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.toString()); // Salva como "YYYY-MM-DD"
            }
        }

        @Override
        public LocalDate read(com.google.gson.stream.JsonReader in) throws IOException {
            // CORREÇÃO: Adiciona checagem de nulo
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            } else {
                return LocalDate.parse(in.nextString()); // Lê como "YYYY-MM-DD"
            }
        }
    }
}