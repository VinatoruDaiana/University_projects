#ifndef PROIECT_H
#define PROIECT_H
#include <opencv2/opencv.hpp>
using namespace std;
using namespace cv;

Mat nearestNeighborResize(const Mat &src, int newWidth, int newHeight);
Mat bilinearResize(const Mat &src, int newWidth, int newHeight);
Mat bicubicResize(const Mat &src, int newWidth, int newHeight);
Mat averageResize(const Mat &src, int newWidth, int newHeight);
void interactiveZoom(const Mat &image);
void adjustZoom(char key, float &zoomFactor);
void displayMethod(int method, const Mat &image, float zoomFactor);



#endif