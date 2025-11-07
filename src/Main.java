// Arquivo: Main.java (Versão Refatorada)

/*
 * A classe Main agora tem UMA ÚNICA responsabilidade:
 * Ser o ponto de entrada (entry-point) e "dar a partida" no programa.
 */
public class Main {

    public static void main(String[] args) {

        // 1. Cria um objeto do tipo InterfaceConsole
        InterfaceConsole minhaInterface = new InterfaceConsole();

        // 2. Chama o método 'iniciar()' desse objeto
        minhaInterface.iniciar();

        // E é só isso! O método 'iniciar()' agora tem
        // o loop 'while' e controla o resto do programa.
    }
}