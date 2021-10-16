import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

class FileReader {

    public static void read(double[][] matrice, double[][] filtru) {
        try {
            File file = new File("date.txt");
            Scanner scanner = new Scanner(file);
            int n, m;
            n = Integer.parseInt(scanner.nextLine());
            m = Integer.parseInt(scanner.nextLine());
            int rowCounter = 0;
            while (n > 0) {
                String line = scanner.nextLine();
                String[] tokens = line.split(" ");
                int columnCounter = 0;
                for (String token : tokens) {
                    matrice[rowCounter][columnCounter++] = Integer.parseInt(token);
                }
                rowCounter++;
                n--;
            }
            int dimFiltru = Integer.parseInt(scanner.nextLine());
            rowCounter = 0;
            while (dimFiltru > 0) {
                String line = scanner.nextLine();
                String[] tokens = line.split(" ");
                int columnCounter = 0;
                for (String token : tokens) {
                    filtru[rowCounter][columnCounter++] = Integer.parseInt(token);
                }
                rowCounter++;
                dimFiltru--;
            }


            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

class FileGenerator {

    public static void generateFile(int n, int m,int dimFiltrare) {
        Random random = new Random();
        try {
            FileWriter fileWriter = new FileWriter("date.txt");
            fileWriter.write(String.valueOf(n) + "\n");
            fileWriter.write(String.valueOf(m) + "\n");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    fileWriter.write(String.valueOf(random.nextInt(10)));
                    if(j != m-1){
                        fileWriter.write(" ");
                    }
                }
                if (i != m - 1) {
                    fileWriter.write("\n");
                }
            }

            fileWriter.write("\n");
            fileWriter.write(String.valueOf(dimFiltrare) + "\n");
            for (int i = 0; i < dimFiltrare; i++) {
                for (int j = 0; j < dimFiltrare; j++) {
                    fileWriter.write(String.valueOf(random.nextInt(10)));
                    if(j != m-1){
                        fileWriter.write(" ");
                    }                }
                if (i != m - 1) {
                    fileWriter.write("\n");
                }
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

class ParalelLiniar implements Runnable{

    private double[][] matrice;
    private double[][] filtru;
    private double[][] rezultat;
    private int start;
    private int stop;

    public ParalelLiniar(double[][] matrice, double[][] filtru, double[][] rezultat, int start, int stop) {
        this.matrice = matrice;
        this.filtru = filtru;
        this.rezultat = rezultat;
        this.start = start;
        this.stop = stop;
    }

    @Override
    public void run() {
            for(int i = start ; i < stop; i++ ){
                for(int j = 0 ; j < matrice[0].length;j++){
                    rezultat[i][j] = Main.calculSecvential(matrice,filtru,filtru[0].length,i,j,matrice[0].length);
                }
            }
    }

}

class ParalelCircular implements Runnable{

    private double[][] matrice;
    private double[][] filtru;
    private double[][] rezultat;
    private int indexStart;
    private int n;
    private int p;

    public ParalelCircular(double[][] matrice, double[][] filtru, double[][] rezultat, int indexStart, int n, int p) {
        this.matrice = matrice;
        this.filtru = filtru;
        this.rezultat = rezultat;
        this.indexStart = indexStart;
        this.n = n;
        this.p = p;
    }

    @Override
    public void run() {
        for ( int i = this.indexStart ; i < n ; i += p){
            for(int j = 0; j < matrice[0].length ; j ++){
                rezultat[i][j] = Main.calculSecvential(matrice,filtru,filtru[0].length,i,j,matrice[0].length);
            }
        }
    }

}

public class Main {

    public static double calculSecvential(double[][] matrice, double[][] filtrare,int dimensiuneMatriceFiltrare,int iElemCurent,int jElemCurent,int dimensiuneMatrice){
        double rez = 0.0;
        int centru = (dimensiuneMatriceFiltrare-1)/2;
        int iFiltrare = 0;
        int jFiltrare;
        for (int i = iElemCurent-centru ; i <= iElemCurent+centru;i++){
            jFiltrare = 0;
            for (int j = jElemCurent-centru ; j <= jElemCurent+centru;j++){
                if(i<0 && j<0){
                    rez = rez + matrice[0][0] * filtrare[iFiltrare][jFiltrare];
                    jFiltrare++;
                }else if(i < 0 && j >=0 && j < dimensiuneMatrice){
                    rez = rez + matrice[0][j] * filtrare[iFiltrare][jFiltrare];
                    jFiltrare++;
                }else if(i < 0 && j >= dimensiuneMatrice){
                    rez = rez + matrice[0][dimensiuneMatrice-1] * filtrare[iFiltrare][jFiltrare];
                    jFiltrare++;
                }else if(i >= 0 && i < dimensiuneMatrice && j < 0){
                    rez = rez + matrice[i][0] * filtrare[iFiltrare][jFiltrare];
                    jFiltrare++;
                }else if(i >= 0 && i < dimensiuneMatrice && j >= dimensiuneMatrice){
                    rez = rez + matrice[i][dimensiuneMatrice-1] * filtrare[iFiltrare][jFiltrare];
                    jFiltrare++;
                }else if(i >= dimensiuneMatrice && j < 0){
                    rez = rez + matrice[dimensiuneMatrice-1][0] * filtrare[iFiltrare][jFiltrare];
                    jFiltrare++;
                }else if(i >= dimensiuneMatrice && j >= 0 && j < dimensiuneMatrice){
                    rez = rez + matrice[dimensiuneMatrice-1][j] * filtrare[iFiltrare][jFiltrare];
                    jFiltrare++;
                }else if(i >= dimensiuneMatrice && j >= dimensiuneMatrice){
                    rez = rez + matrice[dimensiuneMatrice-1][dimensiuneMatrice-1] * filtrare[iFiltrare][jFiltrare];
                    jFiltrare++;
                }else{
                    rez = rez + matrice[i][j] * filtrare[iFiltrare][jFiltrare];
                    jFiltrare++;
                }

            }
            iFiltrare++;
        }

        return rez;
    }

    public static void afisareMatrice(double[][] matrice){
        for (int i = 0 ; i < matrice[0].length; i++){
            for(int j = 0 ; j < matrice[0].length; j++){
                System.out.print(matrice[i][j]+" ");
            }
            System.out.println();
        }
    }

    public static boolean verificareEgalitateMatrice(double[][] a, double[][] b){
        for(int i = 0 ; i < a[0].length ; i ++){
            for(int j = 0 ; j < a[0].length; j++){
                if(a[i][j] != b[i][j]){
                    return false;
                }
            }
        }
        return true;
    }

    public static double[][] calculParalelLiniar(double[][] matrice, double[][] filtru, int n,int p){
        Thread[] threads = new Thread[p];
        double[][] rezultat = new double[matrice[0].length][matrice[0].length];
        int catul = n/p;
        int rest = n%p;
        int start = 0;
        int end;
        for (int i = 0 ; i < p; i++ ){
            if(rest >  0){
                end = start + catul + 1;
                rest --;
            }else{
                end = start + catul;
            }
            threads[i] = new Thread(new ParalelLiniar(matrice, filtru , rezultat ,start,end));
            threads[i].start();
            start = end;

        }
        for(int i = 0 ; i < p ;i ++){
            try {
                threads[i].join();//asteapta toate threadurile sa se termine
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return rezultat;
    }

    public static double[][] calculParalelCiclic(double[][] matrice, double[][] filtru, int n,int p){
        Thread[] threads = new Thread[p];
        double[][] rezultat = new double[matrice[0].length][matrice[0].length];

        for (int i = 0 ; i < p; i++ ){
            threads[i] = new Thread(new ParalelCircular(matrice,filtru,rezultat,i,n,p));
            threads[i].start();
        }

        for(int i = 0 ; i < p ;i ++){
            try {
                threads[i].join();//asteapta toate threadurile sa se termine
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return rezultat;
    }

    public static void main(String[] args) {
        int n = 1000;
        int m = 1000;
        int dimFiltru = 3;
        int numarThreaduri = 2;
        int numarulDeRulari = 10;
        FileGenerator.generateFile(n,m,dimFiltru);
        double[][] matrice = new double[n][m];
        double[][] matriceFiltrataSecvential = new double[n][m];
        double[][] matriceFiltrataParalelLiniar ;
        double[][] matriceFiltrataParalelCircular;
        double[][] filtru = new double[dimFiltru][dimFiltru];
        FileReader.read(matrice,filtru);


        long timpSecvential = 0;
        long timpParalelLiniar = 0;
        long timpParalelCiclic = 0;
        for(int nr = 0 ; nr < numarulDeRulari; nr++){

            long startSecvential = System.nanoTime();
            for(int i = 0 ; i < n; i++){
                for(int j = 0 ; j < m; j++){
                    matriceFiltrataSecvential[i][j] = calculSecvential(matrice,filtru,dimFiltru,i,j,n);;
                }
            }
            long endSecvential = System.nanoTime();
            timpSecvential = timpSecvential + (endSecvential - startSecvential);

            long startParalelLiniar = System.nanoTime();
            matriceFiltrataParalelLiniar = calculParalelLiniar(matrice,filtru,n,numarThreaduri);
            long endParalelLiniar = System.nanoTime();
            timpParalelLiniar = timpParalelLiniar + (endParalelLiniar - startParalelLiniar);

            long startParalelCiclic = System.nanoTime();
            matriceFiltrataParalelCircular = calculParalelCiclic(matrice,filtru,n,numarThreaduri);
            long endParalelCiclic = System.nanoTime();
            timpParalelCiclic = timpParalelCiclic + (endParalelCiclic - startParalelCiclic);

            System.out.println("Transformarea Secventiala si Paralela liniara sunt egale: "+verificareEgalitateMatrice(matriceFiltrataSecvential,matriceFiltrataParalelLiniar));
            System.out.println("Transformarea Secventiala si Paralela circulara sunt egale: "+verificareEgalitateMatrice(matriceFiltrataSecvential,matriceFiltrataParalelCircular));
        }
        long mediaSecvential = timpSecvential / numarulDeRulari;
        long mediaParalelLiniar = timpParalelLiniar / numarulDeRulari;
        long mediaParalelCiclic = timpParalelCiclic / numarulDeRulari;
        System.out.println("Media timpului transformarii secventiale: "+mediaSecvential);
        System.out.println("Media timpului transformarii paralele liniare: "+mediaParalelLiniar);
        System.out.println("Media timpului transformarii paralele ciclic: "+mediaParalelCiclic);

    }
}
