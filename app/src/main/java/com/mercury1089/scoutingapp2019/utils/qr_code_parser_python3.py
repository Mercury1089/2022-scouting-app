import cv2
import qrtools.qrtools
import csv
import time
from shutil import copyfile


def find_qr():

    num_vals = 7 # number of values per game phase
    
    last_input = ""
    SETUP_LIST = "setupList.csv"
    EVENT_LIST = "eventList.csv"
    input_string = ""
    cam = cv2.VideoCapture(0)
    iAr = []
    file = "QRCodes\\123_01.png" # file to save current image to
    while True:
        ret_val, img = cam.read()
        cv2.imshow("1089 Scouting Scanner", img)
        if cv2.waitKey(1) == 27:
            break # break if esc key pressed
        cv2.imwrite(file, img) # save img to file
        qr = qrtools.QR()
        qr.decode(file)
        # check if img contains a qr code
        if qr.data != "NULL" and qr.data != last_input:
            input_string = qr.data
            data_lst = input_string.strip().split(",")
            scouter, match, team = data_lst[:3]
            dst_file = "QRCodes\\"+team+"_"+match+".png"
            copyfile(file, dstfile) # save the img to a more permanant file
            # func to split the data into chunks based on game phases
            chunks = lambda lst, n=num_vals: [lst[i:i+n] \
                                              for i in range(0, len(lst), n)]
            with open(SETUP_LIST, 'w') as csv_file:
                csvWrite = csv.writer(csv_file, dialect='excel', delimiter=',')
                # write pre-game info to setupList.csv
                csvWrite.writerow(data_lst[:15])
            with open(EVENT_LIST, 'w') as csv_file:
                csvWrite = csv.writer(csv_file, dialect='excel', delimiter=',')
                setup_lst = [team, match]
                del data_lst[:15]
                chunklen = len(chunks(data_lst))
                # write values from each game phase to a seperate row
                for chunk in chunks(data_lst):
                    if len(chunk) == num_vals:
                        chunk.append(scouter)
                        chunk.append(time.time())
                        time.sleep(.001)
                        csvWrite.writerow(setup_lst + chunk)
            # prevent scanner from scanning again
            last_input = qr.data
            print("Saved -", scouter, ':', team, ':', match)
            # freeze to give user a chance to move qr code away
            time.sleep(5)
    # close the window showing the img back to the user
    cv2.destroyAllWindows()


def main():
    find_qr()


if __name__ == "__main__":
    main()
