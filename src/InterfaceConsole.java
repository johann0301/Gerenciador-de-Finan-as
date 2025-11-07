// Arquivo: InterfaceConsole.java
import java.util.Scanner;

/*
 * Esta classe é um OBJETO. Ela controla todo o fluxo de
 * interação com o usuário no console.
 * NADA AQUI É STATIC (exceto constantes, se tivéssemos)
 */
public class InterfaceConsole {

    // Atributos: A classe "tem" um gerenciador e um scanner
    private GerenciadorFinanceiro gerenciador;
    private Scanner scanner;

    // Construtor: Quando um objeto InterfaceConsole é criado...
    public InterfaceConsole() {
        // ...ele imediatamente cria seu gerenciador (que vai carregar os dados)
        this.gerenciador = new GerenciadorFinanceiro();
        // ...e cria seu scanner.
        this.scanner = new Scanner(System.in);
    }

    // Método principal que roda o loop
    public void iniciar() {
        int opcao = -1;

        // O loop 'while' que antes estava no Main.java
        while (opcao != 0) {
            System.out.println("\n===== Gestor de Finanças Pessoais =====");
            System.out.println("1. Adicionar Receita");
            System.out.println("2. Adicionar Despesa");
            System.out.println("3. Listar Todas as Transações");
            System.out.println("4. Ver Saldo Atual");
            System.out.println("5. Remover Transação");
            System.out.println("6. Editar Transação");
            System.out.println("7. Gerar Relatório por Categoria");
            System.out.println("0. Sair");
            System.out.println("---------------------------------------");
            // Usamos o 'this.gerenciador' que é um atributo da classe
            System.out.printf("SALDO ATUAL: R$ %.2f\n", this.gerenciador.getSaldo());
            System.out.println("---------------------------------------");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = scanner.nextInt();
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Erro: Por favor, digite um número.");
                scanner.nextLine();
                opcao = -1;
                continue;
            }

            // O 'switch' agora chama métodos da PRÓPRIA classe (this)
            switch (opcao) {
                case 1:
                    adicionarReceita(); // Não é mais static
                    break;
                case 2:
                    adicionarDespesa(); // Não é mais static
                    break;
                case 3:
                    this.gerenciador.listarTransacoes(); // Chama o método do gerenciador
                    break;
                case 4:
                    System.out.printf("Seu saldo atual é: R$ %.2f\n", this.gerenciador.getSaldo());
                    break;
                case 5:
                    removerTransacao(); // Não é mais static
                    break;
                case 6:
                    editarTransacao(); // Não é mais static
                    break;
                case 7:
                    this.gerenciador.gerarRelatorioPorCategoria();
                    break;
                case 0:
                    System.out.println("Saindo do sistema... Obrigado!");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
        // Quando o loop 'while' termina, fechamos o scanner
        scanner.close();
    }

    // --- MÉTODOS AUXILIARES (AGORA NÃO ESTÁTICOS) ---
    // Note que eles não precisam mais receber (Scanner s, Gerenciador g)
    // como parâmetro, pois eles usam os atributos 'this.scanner' e 'this.gerenciador'.

    private void adicionarReceita() {
        System.out.println("\n--- Adicionar Receita ---");
        System.out.print("Valor: R$ ");
        double valor = this.scanner.nextDouble();
        this.scanner.nextLine(); // Limpa o buffer

        System.out.print("Categoria (ex: Salário, Venda): ");
        String categoria = this.scanner.nextLine();

        System.out.print("Data (dd/MM/aaaa): ");
        String data = this.scanner.nextLine();

        System.out.print("Descrição (opcional): ");
        String descricao = this.scanner.nextLine();

        Transacao receita = new Transacao(valor, "RECEITA", categoria, data, descricao);
        this.gerenciador.adicionarTransacao(receita);
    }

    private void adicionarDespesa() {
        System.out.println("\n--- Adicionar Despesa ---");
        System.out.print("Valor: R$ ");
        double valor = this.scanner.nextDouble();
        this.scanner.nextLine();

        System.out.print("Categoria (ex: Alimentação, Transporte): ");
        String categoria = this.scanner.nextLine();

        System.out.print("Data (dd/MM/aaaa): ");
        String data = this.scanner.nextLine();

        System.out.print("Descrição (opcional): ");
        String descricao = this.scanner.nextLine();

        Transacao despesa = new Transacao(valor, "DESPESA", categoria, data, descricao);
        this.gerenciador.adicionarTransacao(despesa);
    }

    private void removerTransacao() {
        System.out.println("\n--- Remover Transação ---");
        this.gerenciador.listarTransacoes();

        System.out.print("Digite o número da transação que deseja remover (0 para cancelar): ");
        int indice = this.scanner.nextInt();
        this.scanner.nextLine();

        if (indice == 0) {
            System.out.println("Remoção cancelada.");
            return;
        }
        this.gerenciador.removerTransacao(indice);
    }

    private void editarTransacao() {
        System.out.println("\n--- Editar Transação ---");
        this.gerenciador.listarTransacoes();

        System.out.print("Digite o número da transação que deseja editar (0 para cancelar): ");
        int indice = this.scanner.nextInt();
        this.scanner.nextLine();

        if (indice == 0) {
            System.out.println("Edição cancelada.");
            return;
        }

        Transacao transacaoParaEditar = this.gerenciador.getTransacaoPorIndice(indice);

        if (transacaoParaEditar == null) {
            System.out.println("Erro: Índice inválido!");
            return;
        }

        System.out.println("Editando: " + transacaoParaEditar);
        System.out.println("Deixe em branco para não alterar.");

        //... (lógica de edição, lendo do 'this.scanner') ...
        // 1. Editar Valor
        System.out.printf("Novo Valor (Atual: %.2f): R$ ", transacaoParaEditar.getValor());
        String novoValorStr = this.scanner.nextLine();
        if (!novoValorStr.isEmpty()) {
            transacaoParaEditar.setValor(Double.parseDouble(novoValorStr));
        }

        // 2. Editar Categoria, Data, Descrição... (igual ao passo anterior)
        // ... (código de edição para os outros campos) ...

        this.gerenciador.salvarAposEdicao();
    }
}