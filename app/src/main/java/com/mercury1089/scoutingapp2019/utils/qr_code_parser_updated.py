import cv2
import qrtools
import csv
import time
from shutil import copyfile

def show_webcam():
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
            break  # press esc to quit
        cv2.imwrite(file, img)
        qr = qrtools.QR()
        qr.decode(file) 
        if qr.data != "NULL" and qr.data != last_input:
            qr_string = qr.data
            game_data = []
            while "HashMapName" in qr_data[1:]:
                hash_map = qr_string[0:qr_string.index("HashMapName",1)-1]
                game_data.append(hash_map.strip().split(','))
                qr_string = qr_string[qr_string.index("HashMapName",1):]
            else:
                game_data.append(qr_data.strip().split(','))
                
            setup_hash = game_data.pop(0)
            scouter = setup_hash[1].split(':')[1]
            match = setup_hash[2].split(':')[1]
            team = setup_hash[3].split(':')[1]
            
            dstfile = "QRCodes\\"+team+"_"+match+".png"
            copyfile(file, dstfile)
            
            with open(SETUP_LIST, 'ab+') as csvfile:
                csv_write = csv.writer(csvfile, dialect='excel', delimiter=',')
                csv_write.writerow(setup_hash)
            with open(EVENT_LIST, 'ab+') as csvfile:
                csv_write = csv.writer(csvfile, dialect='excel', delimiter=',')
                
                setup_arr = [team, match, scouter]

                for phase in game_data:
                    csv_write.writerow(setup_arr + time.time() + phase)
                    time.sleep(.001)
                
            last_input = qr.data
            print "Saved - ", scouter, ":", team,":", match
    cv2.destroyAllWindows()


def main():
    show_webcam()

if __name__ == '__main__':
    main()
