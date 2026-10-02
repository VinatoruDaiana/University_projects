#include <iostream>
#include <opencv2/opencv.hpp>
#include "src/proiect.h"
using namespace std;
using namespace cv;

int main() {
    Mat image = imread("C:\\Users\\Daiana\\Desktop\\AN3_SEM2\\PI\\PROIECT\\Proiect\\imagini\\catel.jpg", IMREAD_GRAYSCALE);

    if (image.empty()) {
        cout << "Nu s-a putut încărca imaginea!" << endl;
        return -1;
    }

    cout << "Dimensiuni originale: " << image.cols << " x " << image.rows << endl;

    interactiveZoom(image);

    return 0;
}
