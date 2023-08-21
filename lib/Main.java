/**
 * @author Joao Vitor Lima de Melo
 *
 */
import java.io.RandomAccessFile;
import java.util.Scanner;
import java.io.File;


public class Main {
    
    // Em resumo, essa classe define as operações de CRUD disponíveis para o sistema de contas:

    /* 

     • inserir();
     • pesquisar();
     • apagar(); 
     • atualizar();
     • transferir();

    */
    public static void main(String[] args){

        // Esse metodo principal é um menu para as operacoes

        System.out.println("==================================");
        System.out.println("1 - Criar uma nova conta bancária.");
        System.out.println("2 - Realizar uma transferência.");
        System.out.println("3 - Buscar uma conta bancária.");
        System.out.println("4 - Atualizar uma conta bancária.");
        System.out.println("5 - Apagar uma conta bancária.");
        System.out.println("6 - Inserções de teste.");
        System.out.println("==================================");
        System.out.print("Número da operação desejada: ");

        Scanner sc = new Scanner(System.in);
        String resp = sc.nextLine();
        try {
            switch(resp) {
                case "1":
                  inserir();
                  break;
                case "2":
                  transferir();
                  break;
                case "3":
                  pesquisar();
                  break;
                case "4":
                  atualizar();
                  break;
                case "5":
                  apagar();
                  break;
                case "6":
                  teste();
                  break;
                default:
                    System.out.println("Erro! Operação inválida!");
            }
        } catch (Exception e) {
            System.out.print("Erro! Operação inválida.");
        }
        sc.close();
    }
    
    // Método que desempenha as funções de leitura para os atributos das contas que serão inseridas 
    // posteriormente no arquivo de dados pela função inserir(nome, email, user, senha, cpf, cidade, saldo).
    public static void inserir(){
        int nEmails;
        String[] email;
        String nome;
        String user;
        String senha;
        String cpf;
        String cidade;
        float saldo;
        Scanner sc = new Scanner(System.in);

        System.out.print("==================================\nCadastro de Nova Conta\nNome: ");
        nome = sc.nextLine();

        System.out.print("Nº de emails: ");
        nEmails = Integer.parseInt(sc.nextLine());
        email = new String[nEmails];
        for (int i = 0; i < nEmails; i++){
            System.out.print((1+i) + "º email: ");
            email[i] = sc.nextLine();
        }

        System.out.print("Username: ");
        user = sc.nextLine();

        System.out.print("Senha: ");
        senha = sc.nextLine();

        System.out.print("CPF: ");
        cpf = sc.nextLine();

        System.out.print("Cidade: ");
        cidade = sc.nextLine();

        System.out.print("Saldo: R$");
        saldo = Float.parseFloat(sc.nextLine());
        
        sc.close();
        System.out.println(inserir(nome, email, user, senha, cpf, cidade, saldo) == true ? "Conta inserida com sucesso!" : "Erro na inserção da conta!");
    }
    
    // O método cria uma instância da estrutura de dados Conta e a transforma em um array de bytes a
    // ser inserido no final do arquivo "contas.db"
    public static boolean inserir(String nome, String[] email, String user, String senha, String cpf, String cidade, float saldo){
        
        boolean resposta = false;
        byte[] ba;
        RandomAccessFile arq;
        try {
            arq = new RandomAccessFile("contas.db", "rw");  
            Conta aux;
            int idConta = 1;   // O ID e iniciado com 1
            if (arq.length() != 0){
                
                // Caso o arquivo contenha algum registro inserido, o ID da conta e igual ao ultimo inserido + 1
                idConta = arq.readInt() + 1;
                arq.seek(0);             // Posiciona o ponteiro no inicio do arquivo
                 
            }
            arq.writeInt(idConta);   // Escreve o ID do registro na 1a posicao
            arq.seek(arq.length());  // Posiciona o ponteiro no fim do arquivo
            aux = new Conta(idConta, nome, email, user, senha, cpf, cidade, 0, saldo);
            ba = aux.toByteArray();
            arq.writeUTF("\" \"");   // Escreve a lapide no arquivo
            arq.writeInt(ba.length); // Escreve o tamano do registro em bytes no arquivo
            arq.write(ba);           // Escreve o registro no arquivo
            arq.close();
            
            resposta = true;
          } catch (Exception e) {
            System.out.println(e.getMessage());
          }
          return resposta;
    }

    // O metodo recebe do usuario o numero do ID do registro que deseja-se apagar para utilizar ele
    // como parametro para a funcao apagar(idBuscado)
    public static void apagar(){

        System.out.print("==================================\nConta a ser apagada\nID: ");
        Scanner sc = new Scanner(System.in);
        int idBuscado = Integer.valueOf(sc.nextLine());
        sc.close();
        System.out.println(apagar(idBuscado) == true ? "Conta apagada com sucesso!" : "Erro na exclusão de conta!");
        
    }
    
    // O metodo procura o endereço do registro de ID no arquivo, insere um asterisco na lápide do registro
    // e chama o metodo limpar()
    public static boolean apagar(int idBuscado){
        boolean resposta = false;
        RandomAccessFile arq;
        try {
            arq = new RandomAccessFile("contas.db", "rw");
            long pos = 4;
            arq.seek(pos);
            while(pos < arq.length()){
                pos = arq.getFilePointer();
                arq.readUTF(); //lapide
                int len = arq.readInt();
                int id = arq.readInt();
                if(id == idBuscado){
                    arq.seek(pos);
                    arq.writeUTF("\"*\"");
                    resposta = true;
                    break;
                }else{
                    pos += len+9;
                    arq.seek(pos);
                }   
            }
            arq.close();
            limpar();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return resposta;
    }

    // Inicialmente, o metodo verifica se o ID fornecido está presente no arquivo, caso positivo, 
    // os bytes do registro da conta em questao sao copiados para um vetor de bytes. Depois, as 
    // informações são expostas da maneira que elas estão armazenadas e o usuario as informa de 
    // maneira atualizada. A conta tem os seus atributos atualizados  e esse registro é escrito 
    // no arquivo na mesma posição do antigo, os registros seguintes foram salvos anterirormente 
    // em um vetor de bytes e são escritos após o término do registro atualizado.
    public static void atualizar(){
        System.out.print("==================================\nConta a ser atualizada\nID: ");
        Scanner sc = new Scanner(System.in);
        int idBuscado = Integer.valueOf(sc.nextLine());
        
        byte[] antigo = null;
        RandomAccessFile arq;
        
        try {
            arq = new RandomAccessFile("contas.db", "rw");
            long fim = arq.getFilePointer(), pos = arq.getFilePointer();
            int id = 0;
            boolean resp = false;
            arq.seek(4); // posiciona o ponteiro na lapide do primeiro registro
            while(pos < arq.length()){
                arq.readUTF(); // lapide
                pos = arq.getFilePointer();
                int len = arq.readInt(); //tamanho do registro em bytes
                id = arq.readInt();
                arq.seek(pos+4);    // reposiciona o ponteiro no ID do registro
                if(id == idBuscado){
                    antigo = new byte[len]; // cria um vetor de bytes do tamanho do registro
                    arq.read(antigo); // copia o registro para o vetor
                    fim = arq.getFilePointer();
                    resp = true;
                    break;
                }else{
                    pos += len+4;
                    arq.seek(pos); // posiciona o ponteiro na lapide do proximo registro
                }   
            }
            
            if(resp == true){
                Conta aux = new Conta();
                aux.fromByteArray(antigo);
                
                /* inicio das operacoes de leitura para atualizacao dos dados */

                System.out.print("==================================\nPreencha as informações atualizadas:\nNome cadastrado: "+ aux.nomePessoa +"\nNome atualizado: ");
                String nome = sc.nextLine();
                System.out.print("Nº de emails cadastrados: " + aux.email.length + "\nNº de emails atualizado: ");
                int nEmails = Integer.parseInt(sc.nextLine());
                String[] email = new String[nEmails];
                for (int i = 0; i < nEmails; i++){
                    if(i < aux.email.length){
                        System.out.print((1+i) + "º email cadastrado: " + aux.email[i]);
                    }
                    System.out.print("\n" + (1+i) + "º email atualizado: ");
                    email[i] = sc.nextLine();
                }
                System.out.print("Username cadastrado: " + aux.nomeUsuario + "\nUsername atualizado: ");
                String user = sc.nextLine();
                System.out.print("Senha cadastrada: " + aux.senha + "\nSenha atualizada: ");
                String senha = sc.nextLine();

                System.out.print("CPF cadastrado: " + aux.cpf + "\nCPF atualizado: ");
                String cpf = sc.nextLine();

                System.out.print("Cidade cadastrada: " + aux.cidade + "\nCidade atualizada: ");
                String cidade = sc.nextLine();

                System.out.print("Saldo cadastrado: R$" + aux.saldoConta + "\nSaldo atualizado: R$");
                Float saldo = Float.parseFloat(sc.nextLine());
                sc.close();
                
                /* fim das operacoes de leitura para atualizacao dos dados */

                aux = new Conta(id, nome, email, user, senha, cpf, cidade, aux.transferenciasRealizadas, saldo); // Cria uma nova instancia do tipo Conta com os dados atuais
                int tam = (int)arq.length();
                byte[] ba = new byte [(int)(tam - fim)]; // Cria um vetor de bytes do tamanho dos registros escritos depois desse
                arq.seek(fim); // Posiciona o ponteiro depois do fim do registro desatualizado
                arq.read(ba); // Copia os registros sequentes para o vetor ba
                arq.seek(pos); // Posiciona o ponteiro na posicao do ID do registro
                byte[] atualizado; 
                atualizado = aux.toByteArray();
                arq.writeInt(atualizado.length); // // Sobrescreve o tamanho do registro antigo em bytes com o novo tamanho
                arq.write(atualizado); // Sobrescreve os atributos da Conta com os dados atualizados
                arq.write(ba); // Sobrescreve o restante dos registros
                if (antigo.length > atualizado.length){
                    // Caso o registro atualizado seja menor que o antigo, o final do arquivo tera lixo de memoria
                    // Portanto, como nao e possivel apagar fisicamente esse lixo, e necessario copiar os registros
                    // para um vetor de bytes, apagar o arquivo "contas.db" e escrever os bytes em um novo arquivo
                    int len = (int)arq.getFilePointer(); // Calcula a posicao do termino do ultimo registro valido
                    ba = new byte[len]; // Constroi um vetor do tamanho de todos os registros validos do arquivo
                    arq.seek(0); // Posiciona o ponteiro no inicio do arquivo
                    arq.read(ba); //Copia os registros para o vetor
                    arq.close();
                    File arquivo = new File("contas.db");
                    arquivo.delete(); // Apaga o arquivo com o lixo
                    arq = new RandomAccessFile("contas.db", "rw"); // Cria um novo arquivo
                    arq.write(ba); // Escreve os bytes dos registros validos no novo arquivo
                }

            }
            System.out.println((resp == true ) ? ("Atualização efetuada!") : ("ID não encontrado!"));
            arq.close();             
            
          } catch (Exception e) {
            System.out.println(e.getMessage());
          }
    }

    // Método que desempenha as funções de leitura para o ID das contas envolvidas na transferencia 
    // e o valor dela. Esses dados serao utilizados como parametro para a funcao transferir(emissor,
    // destino, valor).
    public static void transferir(){
        
        System.out.print("==================================\nID da Conta Emissora da Transferência: ");
        Scanner sc = new Scanner(System.in);
        int emissor = Integer.valueOf(sc.nextLine());
        System.out.print("ID da Conta Destino da Transferência: ");
        int destino = Integer.valueOf(sc.nextLine());
        if (emissor == destino){
            System.out.println("Erro! Transação não pode ser efetuada!");
        }else{
            System.out.print("Valor da Transferência: ");
            float valor = Float.parseFloat(sc.nextLine());
            System.out.println(transferir(emissor, destino, valor) == true ? "Transação efetuada com sucesso!" : "Erro na execução da transferência!");
        }
        sc.close();
                
    }

    // O método verifica o se ha registros de contas com os IDs fornecidos. Caso positivo, Os valores
    // de saldo e numero de transferências são extraidos, atualizados e sobrescritos na mesma posição do arquivo.
    public static boolean transferir(int emissor, int destino, float valor){
        RandomAccessFile arq;
        boolean resultado = false;
        try {
            arq = new RandomAccessFile("contas.db", "rw");
            arq.readInt();
            long pos = arq.getFilePointer();
            int total = 0;
            while(pos < arq.length() && total < 2){ // O laco repete ate que se encontre registros com os IDs emissor e destino
                arq.readUTF(); // lapide
                int len = arq.readInt(); // tamanho do registro em bytes
                pos = arq.getFilePointer();
                int id = arq.readInt(); 
                if(id == emissor || id == destino){
                    ++total;
                }
                pos += len;
                arq.seek(pos); // posiciona o ponteiro na lapide do primeiro registro
            }
            if(total == 2){ // caso o arquivo contenha os dois IDs
                pos = 4;
                arq.seek(pos); // posiciona o ponteiro na lapide do primeiro registro
                while(pos < arq.length() && total != 0){
                    arq.readUTF(); // lapide
                    int len = arq.readInt(); // tamanho do arquivo em bytes
                    pos = arq.getFilePointer();
                    int id = arq.readInt();
                    if(id == emissor){
                        pos += len - 8;
                        arq.seek(pos); // posiciona o ponteiro no numero de transferencias do emissor 
                        int transferencias = arq.readInt() + 1; // atualiza o numero de transferencias
                        float saldo = arq.readFloat() - valor; // atualiza o saldo da conta
                        arq.seek(pos); // retorna o ponteiro no numero de transferencias do emissor
                        arq.writeInt(transferencias); // escreve o numero atualizado de transferencias
                        arq.writeFloat(saldo); // escreve o saldo atualizado da conta
                        total--; // atualiza o contador
                    }else if (id == destino){
                        pos += len - 8;
                        arq.seek(pos); // posiciona o ponteiro no numero de transferencias do destino
                        int transferencias = arq.readInt() + 1; // atualiza o numero de transferencias
                        float saldo = arq.readFloat() + valor; // atualiza o saldo da conta
                        arq.seek(pos); // retorna o ponteiro no numero de transferencias do destino
                        arq.writeInt(transferencias); // escreve o numero atualizado de transferencias
                        arq.writeFloat(saldo); // escreve o saldo atualizado da conta
                        total--; // atualiza o contador
                    }else{
                        pos += len;
                        arq.seek(pos); // posiciona o ponteiro na lapide do proximo registro
                    }
                }
                resultado = total == 0;
            
            } else {
                System.out.print("Uma das contas não pode ser encontrada. ");
            }
            arq.close(); 
            
          } catch (Exception e) {
            System.out.println(e.getMessage());
          }
          return resultado;
    }

    // Método que desempenha a função de leitura do ID da contas a ser pesquisada. Esse dado sera
    // utilizado como parametro para a funcao pesquisar(idBuscado).
    public static void pesquisar(){
        System.out.print("==================================\nBusca de Conta Bancária\nID: ");
        Scanner sc = new Scanner(System.in);
        int idBuscado = Integer.valueOf(sc.nextLine());
        sc.close();
        pesquisar(idBuscado);
    }

    // O método percorre o arquivo em busca do registro com o ID passado por parametro. Em seguida,
    // o conteúdo escrito no arquivo é lido para um array e convertido no tipo Conta para ter os
    // atributos impressos na tela.
    public static void pesquisar(int idBuscado){
        
        byte[] ba;
        RandomAccessFile arq;

        try {
            arq = new RandomAccessFile("contas.db", "rw");
            arq.readInt(); // Numero de registros no arquivo
            Conta tmp = new Conta();
            long pos = arq.getFilePointer();
            while(pos < arq.length()){
                arq.readUTF(); //lapide
                int len = arq.readInt(); // tamanho do registro em bytes
                pos = arq.getFilePointer();
                int id = arq.readInt();
                arq.seek(pos); // posiciona o ponteiro no ID do registro
                if(id == idBuscado){
                    ba = new byte[len]; // cria um vetor de bytes do tamanho do registro
                    arq.read(ba); // copia os bytes do registro para o vetor
                    tmp.fromByteArray(ba); // transforma vetor de bytes em uma conta
                    break;
                }else{
                    pos += len;
                    arq.seek(pos); // posiciona o ponteiro no proximo registro
                }  
            }
            arq.close(); 
            System.out.println((tmp.idConta > -1) ? (tmp) : ("ID não encontrado!"));
            
          } catch (Exception e) {
            System.out.println(e.getMessage());
          }
    }

    // Método varre o arquivo de contas buscando algum registro cuja lápide contenha um asterisco, 
    // os registros localizados após esse a ser apagado serão sobrescritos em sua posicao. Depois,
    // os registros serão realocados em um novo arquivo, sendo esse atual apagado.
    public static void limpar(){
        RandomAccessFile arq;
        try {
            arq = new RandomAccessFile("contas.db", "rw");
            arq.readInt();
            long pos = arq.getFilePointer();
            boolean registroApagado = false;
            int len = 0;
            while(registroApagado == false){
                // esse laco percorre o arquivo ate encontrar o registro com um asterisco na lapide
                String lapide = arq.readUTF();
                len = arq.readInt(); // tamanho do registro em bytes
                if(lapide.compareTo("\"*\"") == 0){
                    registroApagado = true;
                }else{
                    // caso a lapde nao contenha um asterisco o ponteiro sera posicionado no proximo registro
                    pos = arq.getFilePointer();
                    pos += len; // o proximo registro encontra-se len bytes apos a posicao inicial
                    arq.seek(pos);
                }
            }
            byte[] ba = new byte[(int)arq.length()-(len+9)]; // constroi um vetor de bytes de tamanho = tamanho do arquivo - tamanho do registro apagado
            arq.seek(pos + 9 + len); // posiciona o ponteiro para o registro seguinte do arquivo
            arq.read(ba); // copia o restante de registros do arquivo para o vetor
            arq.seek(pos); // posiciona o ponteiro para o registro apagado
            arq.write(ba); // sobrescreve o registro apagado pelos registros seguintes
            // lixos de memoria ficam no final do arquivo, nesse sentido e necessario copiar 
            byte ba3[] = new byte [(int)arq.getFilePointer()]; // constroi um vetor de bytes de tamanho igual a posicao do ponteiro, ou seja, apos o ultimo byte do ultimo registro
            arq.seek(0); // posiciona o ponteiro no inicio do arquivo
            arq.read(ba3); // copia os bytes dos registros para o vetor
            arq.close();
            File arquivo = new File("contas.db"); 
            arquivo.delete(); // apaga o arquivo de contas
            arq = new RandomAccessFile("contas.db", "rw"); // cria um novo arquivo
            arq.write(ba3); // escreve os registros no arquivo
            arq.close();
            
            arq.close();         
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
 
    // Utiliza dos métodos de inserção, remoção e pesquisa para testar o sistema implementado com 
    // dados estruturados.
    public static void teste(){
        String email[] = new String[2];
        email[0] = "jvlimademelo@gmail.com";
        email[1] = "joao@mail.com";
        inserir("João Vitor", email, "joaovitor", "joao123", "11111111111", "Belo Horizonte", 1500);
        email = new String[1];
        email[0] = "aclara@outlook.com";
        inserir("Ana Clara", email, "aclara", "@n@#cl@r@", "22222222222", "Rio de Janeiro", 2000);
        email = new String[2];
        email[0] = "luisgustavo@gmail.com";
        email[1] = "lg@hotmail.com";
        inserir("Luis Gustavo", email, "luisgg", "Lu1s87#92", "33333333333", "Brasília", 700);
        email = new String[1];
        email[0] = "carolina@email.com";
        inserir("Carolina", email, "caroll", "#Carol123", "44444444444", "Salvador", 900);
        email[0] = "juliasts@mail.com";
        inserir("Júlia Santos", email, "jusantos", "Juli@sts123", "55555555555", "Belém", 3000);
        email[0] = "samuel@outlook.com";
        inserir("Samuel", email, "samuca", "S4mu3l#21", "66666666666", "São Paulo", 1000);
        email[0] = "ph@gmail.com";
        inserir("Pedro Henrique", email, "pedroh", "PH#2022", "77777777777", "Brasília", 2000);
        email = new String[3];
        email[0] = "alice@gmail.com";
        email[1] = "alice@hotmail.com";
        email[2] = "alicedias@email.com";
        inserir("Alice Dias", email, "aliced", "DiasAlice765", "88888888888", "Curitiba", 600);
        email = new String[1];
        email[0] = "fernanda@contato.com";
        inserir("Fernanda Brandão", email, "febrandao", "fe#bra123", "99999999999", "Recife", 2500);
        email = new String[1];
        email[0] = "laura@gmail.com";
        inserir("Laura", email, "laura", "laura@laura", "00000000000", "Florianópolis", 910);
        email[0] = "bia@gmail.com";
        inserir("Ana Beatriz", email, "bia123", "bia123", "12312345645", "Recife", 823);
        email[0] = "sofia@hotmail.com";
        inserir("Sofia Santos", email, "sofiasantos", "Sofi@S#123", "98976863546", "Brasília", 1300);
        email[0] = "ricardo@mail.com";
        inserir("Ricardo Mendes", email, "ricMendes", "r1c4rd0#170", "01719836728", "Belo Horizonte", 790);
        email[0] = "amgomes@outlook.com";
        inserir("Amanda Gomes", email, "amGomes", "amG0m3s#", "62626374651", "São Paulo", 612);
        email[0] = "jorgeluiz@email.com";
        inserir("Jorge Luiz", email, "jorge421", "jorge#421", "76543215678", "Rio de Janeiro", 345);
        email[0] = "lais.montes@hotlook.com";
        inserir("Lais Montes", email, "montesLais", "montes#Lais", "765443266", "Salvador", 440);
        email[0] = "lucbrnd@hotmail.com";
        inserir("Lucas Brandão", email, "lucbrnd#221", "lucbrnd12#422", "98765447811", "Brasília", 980);
        email[0] = "pv@mail.com";
        inserir("Paulo Vitor", email, "pv#123@", "pv#123@", "76588897652", "", 781);
        email[0] = "lets@mail.com";
        inserir("Leticia Maia", email, "letsmaia", "2002202#ns", "55463176912", "", 1000);
        email[0] = "maria@gmail.com";
        inserir("Maria Carvalho", email, "", "", "54371612341", "Manaus", 1200);
        
        apagar(8);
        pesquisar(1);
        pesquisar(2);
        pesquisar(3);
        pesquisar(4);
        pesquisar(5);
        pesquisar(6);
        pesquisar(7);
        pesquisar(8);
        pesquisar(9);
        pesquisar(10);
    }
}