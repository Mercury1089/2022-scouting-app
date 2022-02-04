# -*- coding: cp1252 -*-
import cv2
import qrtools
import csv
import time
from shutil import copyfile

def show_webcam():

    num_setup = 13
    num_vals = 7 # number of values per game phase

    last_input = ""
    SETUP_LIST = 'setupList.csv'
    EVENT_LIST = 'eventList.csv'
    cam = cv2.VideoCapture(0)
    iAr = []
    file = "QRCodes\\123_01.png"
    while True:
        ret_val, img = cam.read()
        cv2.imshow('1089 Scouting Scanner', img)
        if cv2.waitKey(1) == 27:
            break  # esc to quit
        cv2.imwrite(file, img)
        qr = qrtools.QR()
        qr.decode(file)
        if qr.data != "NULL" and qr.data != last_input:
            iAr = qr.data.strip().split(",")
            scouter=iAr[0]
            match=iAr[1]
            team=iAr[2]
            dstfile = "QRCodes\\"+team+"_"+match+".png"
            copyfile(file, dstfile)
            chunks = lambda iAr, n=num_vals: [iAr[i:i+n] \
                                              for i in range(0, len(iAr), n)]
            with open(SETUP_LIST, 'ab+') as csvfile:
                csv_write = csv.writer(csvfile, dialect='excel', delimiter=',')
                csv_write.writerow(iAr[:num_setup])
            with open(EVENT_LIST, 'ab+') as csvfile:
                csv_write = csv.writer(csvfile, dialect='excel', delimiter=',')
                setup_arr = [team, match, "Game Phase"]
                del iAr[:num_setup]
                for chunk in chunks(iAr):
                    print chunk
                    if len(chunk) < num_vals:
                        break
                    setup_arr[2] = chunk[0]
                    csv_write.writerow(setup_arr+["S", "I", chunk[1], scouter])
                    csv_write.writerow(setup_arr+["S", "O", chunk[2], scouter])
                    csv_write.writerow(setup_arr+["S", "L", chunk[3], scouter])
                    csv_write.writerow(setup_arr+["M", "H", chunk[4], scouter])
                    csv_write.writerow(setup_arr+["M", "L", chunk[5], scouter])
                    csv_write.writerow(setup_arr+["D", "", chunk[6], scouter])
            last_input = qr.data
            print "Saved - ", scouter, ":", team,":", match
    cv2.destroyAllWindows()


def main():
    show_webcam()

if __name__ == '__main__':
    main()

