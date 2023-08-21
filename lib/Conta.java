/**
 * @author Joao Vitor Lima de Melo
 *
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;

import java.io.IOException;
import java.text.DecimalFormat;

public class Conta{

    // Em resumo, essa classe define os atributos de uma Conta e os  metodos  de 
    // transformacao de um array de bytes em uma Conta e de transformacao de uma
    // Conta em um array de bytes

    protected int idConta;
    protected String nomePessoa; 
    protected String email[]; 
    protected String nomeUsuario;
    protected String senha; 
    protected String cpf;
    protected String cidade;
    protected int transferenciasRealizadas;
    protected float saldoConta;

    // Construtor com todos os parametos necessarios
    public Conta(int id, String nome, String[] array, String user, String password, String cpf, String cidade, int transferencias, float saldo) {
        idConta = id;
        nomePessoa = nome;
        int tam = array.length;
        email = new String[tam]; 
        for(int i = 0; i < tam; i++){
            email[i] = array[i];
        }
        nomeUsuario = user;
        senha = password;
        this.cpf = cpf;
        this.cidade = cidade;
        transferenciasRealizadas = transferencias;
        saldoConta = saldo;
    }

    // Construtor vazio
    public Conta(){    
        idConta = -1;
        nomePessoa = "";
        email = null;
        nomeUsuario = "";
        senha = "";
        cpf = "";
        cidade = "";
        transferenciasRealizadas = -1;
        saldoConta = -1;
    }
    
    public String toString(){
        DecimalFormat df= new DecimalFormat("#,##0.00");
        String resposta = "\nID: " + idConta +
        "\nNome: " + nomePessoa;
        int tam = email.length;
        for(int i = 0; i < tam; i++){
            resposta += "\nEmail " + (1+i) + " : " + email[i];
        }
        resposta += "\nUser: " + nomeUsuario + 
        "\nSenha: " + senha + 
        "\nCPF: " + cpf +
        "\nCidade: " + cidade +
        "\nTransferencias: " + transferenciasRealizadas +
        "\nSaldo: R$"+ df.format(saldoConta);

        return resposta;
                
    }

    // O metodo retorna a Conta em vetor de bytes
    public byte[] toByteArray() throws IOException{

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        
        dos.writeInt(idConta);
        dos.writeUTF(nomePessoa);
        int tam = email.length;
        dos.writeInt(tam);
        for(int i = 0; i < tam; i++){
            dos.writeUTF(email[i]);
        }
        dos.writeUTF(nomeUsuario);
        dos.writeUTF(senha);
        dos.writeUTF(cpf);
        dos.writeUTF(cidade);
        dos.writeInt(transferenciasRealizadas);
        dos.writeFloat(saldoConta);

        return baos.toByteArray();
    }

    // O metodo constroi uma Conta a partir de um vetor de bytes
    public void fromByteArray(byte ba[]) throws IOException{

        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        idConta = dis.readInt();
        nomePessoa = dis.readUTF();
        int qtd = dis.readInt();
        email = new String[qtd];
        for (int i = 0; i < qtd; i++){
            email[i] = dis.readUTF();
        }
        nomeUsuario = dis.readUTF();
        senha = dis.readUTF();
        cpf = dis.readUTF();
        cidade = dis.readUTF();
        transferenciasRealizadas = dis.readInt();
        saldoConta = dis.readFloat();
    }
    
}