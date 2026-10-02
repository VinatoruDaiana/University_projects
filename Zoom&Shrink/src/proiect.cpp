
#include <iostream>
#include <opencv2/opencv.hpp>
#include <fstream>
#include "proiect.h"

using namespace std;
using namespace cv;


//functia primeste ca si parametrii imaginea originala=source
//newWidth,newHeight=dimensiuni noi ale imagnii
Mat nearestNeighborResize(const Mat &source, int newWidth, int newHeight)
{
     //matrice goala de aceleasi dimensiuni ca cea originala
    //stochez rez in ea
    Mat dst(newHeight, newWidth, source.type());

    //x_ratio spune cati pixeli originali acopera pixelul nou
    //calculez raportul dintre dimensiunile originale si cele noi
    //fiecare pixel din img noua coresp unui pixel din img veche
    //img originala : 100 x 100 si ing noua : 50x 50
    // x_ratio=y_ratio=2.0
    //1 pixel din img noua acopera 2 pixeli din img veche
    //sunt declarate float ca sa nu pierd partea zecimala
    // ex : 100/200=0.5 si x_ratio ar fi int as avea x_ratio=0
    float x_ratio = float(source.cols) / newWidth;
    float y_ratio = float(source.rows) / newHeight;

    //parcurg toti pixelii din imaginea finala
    for (int i = 0; i < newHeight; i++) {
        for (int j = 0; j < newWidth; j++) {
            //calculez ce pixel din img originala coresp poz (i,j) in imaginea noua
            //ce pixel din img originala copiez in img noua
            //int ul rotunjeste la cel mai din stanga pixel pe X
            //si cel mai de sus pixel pe Y
            int src_x = int(j * x_ratio);
            int src_y = int(i * y_ratio);

            //in matricea destinatie pun pixelul coresp din matricea initiala
            //at<> asteapta itregi nu float
            dst.at<uchar>(i, j) = source.at<uchar>(src_y, src_x);
        }
    }

    return dst;
}

//functia primeste aceeasi param ca inainte
Mat bilinearResize(const Mat &source, int newWidth, int newHeight)
{
    //creez mat dst in care voi pune rez final
    Mat dst(newHeight, newWidth, source.type());

    //x_ratio si y_raio spun cati pixeli originali acopera pixelul nou
    //aici scad 1 pt ca eu ma uit la spatiul dintre pixeli acum ,nu direct la pixel ca punct discret
    //la celelalte metode se maap direct pixeluul nou catre cel original
    //aici facand interpolare ma intereseaza interiorul pixelilor,distanta intre ei
    //scazand 1 calculez nr de intervale,nu nr de puncte
    float x_ratio = float(source.cols - 1) / newWidth;
    float y_ratio = float(source.rows - 1) / newHeight;

    for (int i = 0; i < newHeight; i++) {
        for (int j = 0; j < newWidth; j++) {

            //calculez ce pixel din imag originala coresp pixelul (i,j) in img noua
            float x = j * x_ratio;
            float y = i * y_ratio;

            //gasesc cei 4 vecini originali care inconjoara punctul (x,y)
            int x0 = int(x);//pixelul de la stanga
            int y0 = int(y);//pixelul de sus
            int x1 = min(x0 + 1, source.cols - 1);//dreapta
            int y1 = min(y0 + 1, source.rows - 1);//jos

            //calculez cat de departe este punctul fata de coltul
            //stanga sus
            float dx = x - x0;
            float dy = y - y0;

            //extrag val pixelilor din cei 4 vecini care inconjoara
            //punctul pe care l-am calculat mai sus
            //astae sunt val efective ale pixelilor
            // ma ajuta mai apoi sa calculez media
            //val finala a pixelului este influentata de cat de apropae e punctul fata de unul din colturi
            //daca  e mai aproape de stanga sus I00 conteaza mai mult
            float I00 = source.at<uchar>(y0, x0);//stanga sus
            float I10 = source.at<uchar>(y0, x1);//dreapta sus
            float I01 = source.at<uchar>(y1, x0);//stanga jos
            float I11 = source.at<uchar>(y1, x1);//dreapta jos

            //formula de interpolare biliniara
            //1-dx  = cat de aproape de stanga sus
            //dx= cat de aproape de dreapta
            //1-dy  = cat de aproape de sus sunt
            //dy = cat de aproape de jos sunt


            //(1 - dx) * (1 - dy) stanga sus
            //dx * (1 - dy) * I10 dreapta sus
            //(1 - dx) * dy * I01 stanga jos
            //dx * dy * I11; dreapta jos
            float I = (1 - dx) * (1 - dy) * I00 +
                      dx * (1 - dy) * I10 +
                      (1 - dx) * dy * I01 +
                      dx * dy * I11;

            //convertesc val I la uchar care era float
            //daca I era 129.4 acum este 129
            //pun acesta val in matricea destinatie la pixelul (i,j)
            dst.at<uchar>(i, j) = uchar(I);
        }
    }

    return dst;
}

//functia primeste ca si parm v0,v1,v2,v3=val celor 4 pixeli vecini
//si x care este poz fractionara intre v1 si v2
//ret val interpolata intre v1 si v2,care este calculata cu ajutorul celor
//4 puncte  si a lui x
// daca x=0 cade pe v1 , daca x=1 cade pe v2 , daca x=0,5 cade intre v1 si v2
float cubicInterpolate(float v0, float v1, float v2, float v3, float x) {
    return v1 + 0.5 * x*(v2 - v0 + x*(2.0*v0 - 5.0*v1 + 4.0*v2 - v3 + x*(3.0*(v1 - v2) + v3 - v0)));
}

Mat bicubicResize(const Mat &source, int newWidth, int newHeight)
{
    Mat dst(newHeight, newWidth, source.type());

    //x_ratio si y_raio spun cati pixeli originali acopera pixelul nou
    float x_ratio = float(source.cols) / newWidth;
    float y_ratio = float(source.rows) / newHeight;

    for (int i = 0; i < newHeight; i++) {
        for (int j = 0; j < newWidth; j++) {

            //det poz pixelilor in imag originala
            //vad unde pica pixelul nou in img originala
            float x = j * x_ratio;
            float y = i * y_ratio;

            //x este poz reala pe axa X in img originala
            //int(x) ia doar partea intreaga
            //la fel pt y
            int x_int = int(x);
            int y_int = int(y);
            //calculez partea fractionara
            //dif dintre x ul original si partea intreaga
            //cat de departe este punctul fata de pixelul original
            float x_frac = x - x_int;
            //la fel pt y
            float y_frac = y - y_int;

            float col[4];//vector ce stocheaza rez interpolarii pe orizontala
            //parcurg 4 randuri pe orizontala
            //de la y_int-1 pana la y_int+2
            //pt ca am nevoie de 4x4 pixeli in jurul punctului
            //parcurg pe randuri prima data
            for (int m = -1; m <= 2; m++) {
                float row[4];//vector pt fiecare rand unde pun pixelii orizontali pt acel rand
                //parcurg 4 coloane in jurul punctului
                //de la x_int-1 pana la x_int+2
                //parcurg pe coloane apoi
                for (int n = -1; n <= 2; n++) {
                    //calculez coord efective ale pixelului
                    //x_int+n =poz pe axa X deplasat cu n
                    //y_int +m =poz pe axa Y deplasata cu m
                    //max indica daca am iesit sub 0
                    //min daca am sarit peste marginea dreapta ,ma limiteaza la ultimul pixel
                    //coloana
                    int px = min(max(x_int + n, 0), source.cols - 1);
                    //randul
                    int py = min(max(y_int + m, 0), source.rows - 1);
                    //stochez val pixelului in vec row pe randuri
                    row[n + 1] = source.at<uchar>(py, px);
                }
                //pt foecare din cele 4 randuri, fac interolare cubica pe orizontala
                //ontin 4 val stocate in col
                //apoi fac interpolarea cubica pe verticala cu ele so astfel obtin
                //val finala a punctului
                col[m + 1] = cubicInterpolate(row[0], row[1], row[2], row[3], x_frac);
            }

            //am calculat deja pt pixelul (i,j) col[0], col[1], col[2], col[3]
            //cele 4 val obtinute prin interpolare
            //acum aplic interpolarea cubica pe verticala  intre cele 4 val obtinute
            //mai susu pe orizontala
            //astfel obtin val finala a pixelului
            float value = cubicInterpolate(col[0], col[1], col[2], col[3], y_frac);
            //ma asigur ca val calculatat nu coboara sub 0 sau trece peste 255
            value = min(max(value, 0.0f), 255.0f);

            //ii dau matricei destinaetie val finalal a fiecarui pixel
            dst.at<uchar>(i, j) = uchar(value);
        }
    }

    return dst;
}


//daca vreau sa fac o imagine din 2x2 la 4x4 voi avea x_ratio<1
//adica fiecare pixel nou acopera mai putin de 1 pixel original
//iar metoda asta se bazeaza ca blocul nou acopera >=1 pixel original
//ca sa poata sa faca media intre pixeli
Mat averageResize(const Mat &source, int newWidth, int newHeight) {
    Mat dst(newHeight, newWidth, source.type());

    //cati pixeli din matricea originla corespund unui pixel nou
    float x_ratio = float(source.cols) / newWidth;
    float y_ratio = float(source.rows) / newHeight;

    //parcurg matricea noua
    for (int i = 0; i < newHeight; i++) {
        for (int j = 0; j < newWidth; j++) {

            //calculez poz de start pe axa X in img originala
            //pt pixelulu j din img noua ,unde j index coloana
            int x_start = int(j * x_ratio);
            //la fel si pt axa Y
            int y_start = int(i * y_ratio);
            //calculez poz de sf pe axa X
            //(j + 1) * x_ratio) unde se termina blocul in img originala
            //min ca sa nu se iese din matrice
            int x_end = min(int((j + 1) * x_ratio), source.cols);
            int y_end = min(int((i + 1) * y_ratio), source.rows);

            int sum = 0;//suma pixelilor din blocul curent
            int count = 0;//cati pixeli adun ca sa fac media

            //accesez prima data linia si apoi coloana
            //parcurg pixelii din blocul original
            for (int y = y_start; y < y_end; y++) {
                for (int x = x_start; x < x_end; x++) {
                    //fac suma tuturor pixelilor
                    sum += source.at<uchar>(y, x);
                    ++count;//nr cati pixeli adun
                }
            }

            //in matricea finala fac media dintre suma si nr de pixeli adunati
            dst.at<uchar>(i, j) = uchar(sum / count);
        }
    }

    return dst;
}


void adjustZoom(char key, float &zoomFactor)
{
    if (key == '+') {
        zoomFactor *= 1.2f;
        cout << "Zoom in: factor=" << zoomFactor << endl;
    }
    if (key == '-') {
        zoomFactor /= 1.2f;
        cout << "Zoom out: factor=" << zoomFactor << endl;
    }
}

void displayMethod(int method, const Mat &image, float zoomFactor)
{
    int newWidth = max(1, int(image.cols * zoomFactor));
    int newHeight = max(1, int(image.rows * zoomFactor));
    Mat resized;

    if (method == 1) {
        resized = nearestNeighborResize(image, newWidth, newHeight);
        cout << "Metoda: Nearest Neighbor" << endl;
    } else if (method == 2) {
        resized = bilinearResize(image, newWidth, newHeight);
        cout << "Metoda: Bilinear" << endl;
    } else if (method == 3) {
        resized = bicubicResize(image, newWidth, newHeight);
        cout << "Metoda: Bicubic" << endl;
    } else if (method == 4) {
        resized = averageResize(image, newWidth, newHeight);
        cout << "Metoda: Average (shrink)" << endl;
    }

    Mat display;
    //daca am imagine mare si nu incape pe ecran
    if (resized.cols > 1000 || resized.rows > 800) {
        //o redimesnionez la jumatate
        resize(resized, display, Size(), 0.5, 0.5);
    } else {
        display = resized;
    }

    imshow("Zoom Viewer", display);
}

void interactiveZoom(const Mat &image) {
    int currentMethod = 1;
    float zoomFactor = 1.0f;

    cout << "Taste disponibile:" << endl;
    cout << "1 - Nearest Neighbor" << endl;
    cout << "2 - Bilinear" << endl;
    cout << "3 - Bicubic" << endl;
    cout << "4 - Average (shrink only)" << endl;
    cout << "+ -> Zoom in" << endl;
    cout << "- -> Zoom out" << endl;
    cout << "ESC - Iesire" << endl;

    while (true) {
        displayMethod(currentMethod, image, zoomFactor);

        char key = (char)waitKey(0);

        if (key == 27) break;  // ESC
        adjustZoom(key, zoomFactor);

        if (key == '1') currentMethod = 1;
        if (key == '2') currentMethod = 2;
        if (key == '3') currentMethod = 3;
        if (key == '4') currentMethod = 4;
    }

    destroyAllWindows();
}
