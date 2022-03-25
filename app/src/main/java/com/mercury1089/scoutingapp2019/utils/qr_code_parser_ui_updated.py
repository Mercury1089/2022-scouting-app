import pygame, sys, cv2, qrtools, csv, time
from shutil import copyfile
from pygame.locals import *
from Utils import *

pygame.init()

DISPLAY_WIDTH = 1200
DISPLAY_HEIGHT = 650
FPS = 30

FPS_CLOCK = pygame.time.Clock()
DISPLAY_SURF = pygame.display.set_mode((DISPLAY_WIDTH, DISPLAY_HEIGHT))
pygame.display.set_caption("QR Code Parser")

SCAN_SCREEN = "scan screen"
PREVIOUS_CODES_SCREEN = "previous codes"
current_screen = SCAN_SCREEN

num_match = 3 # scouter, match, team
num_setup = 5
num_vals = 7 # number of values per game phase

last_input = ""
previous_inputs = []
previous_inputs_full = []
SETUP_LIST = 'setupList.csv'
EVENT_LIST = 'eventList.csv'
QR_STRINGS = 'qrStrings.txt'
cam = cv2.VideoCapture(0)
file = "QRCodes\\temp.png"
match_number = 0

def terminate():
    cam.release()
    pygame.quit()
    sys.exit()

def setup_previous_inputs():
    global previous_inputs, previous_inputs_full
    with open(QR_STRINGS, 'r') as in_file:
        previous_inputs_full = in_file.readlines()
    for qr_string in previous_inputs_full:
        previous_inputs_full[
            previous_inputs_full.index(qr_string)] = \
            qr_string[:qr_string.rindex(',')+1]
        qr_data = qr_string.split(',')
        scouter, match, team = qr_data[:3]
        previous_inputs.append([scouter, team, match])

def setup_webcam_screen():
    global text_boxes, focus, mouse_clicked, mouse_pos, next_match_button, \
           match_number_lable, match_number_textbox
    text_boxes = []
    focus = -1
    mouse_clicked = False
    mouse_pos = (0,0)
    next_match_button = pygame.Rect(DISPLAY_WIDTH - 350, 500, 200, 40)
    match_number_textbox = Text_Box((DISPLAY_WIDTH - 200, 400),size=(100,30),
                                    text=str(match_number))
    match_number_lable = Text_Box((DISPLAY_WIDTH - 400, 400), size=(300,30),
                                  text="Match Number:")
    top_pos = 100
    for i in range(6):
        text_boxes.append([Text_Box((DISPLAY_WIDTH - 400, top_pos)), i])
        top_pos += 30

    for tb in text_boxes:
        tb[0].set_bg_color(RED)

def decode_qr_code():
    global last_input
    scanned = True
    skip_match_and_team_check = False
    qr = qrtools.QR()
    qr.decode(file)
    if qr.data != "" and qr.data != "NULL" and qr.data != last_input:
        iAr = qr.data.strip().split(",")
        if len(iAr) < num_match + num_setup + num_vals:
            raise ValueError("incompatable qr code: " + qr.data)

        scouter=iAr[0]
        match=iAr[1]
        team=iAr[2]

        for tb in text_boxes:
            expected = tb[0].get_text().lower()
            actual = scouter.lower()
            if expected != "" and (actual in expected or expected in actual):
                tb[0].set_bg_color(GREEN)
                break
        else:
            original_surf = DISPLAY_SURF.copy()
            popup_window = Popup("Uh Oh", "You done f***ed up!", buttons="ok",
                                 pos=(DISPLAY_WIDTH//2,DISPLAY_HEIGHT//2),
                                 pos_type="center")
            popup_window.show(DISPLAY_SURF, FPS)
            DISPLAY_SURF.blit(original_surf, (0,0))
            pygame.display.update()
            last_input = qr.data
            return False
        
        if qr.data in previous_inputs_full:
            original_surf = DISPLAY_SURF.copy()
            popup_window = Popup("Duplicate QR Code",
                                 "This QR Code has already been scanned.",
                                 buttons="ok", size=(300,150),
                                 pos=(DISPLAY_WIDTH//2,DISPLAY_HEIGHT//2),
                                 pos_type="center")
            if popup_window.show(DISPLAY_SURF, FPS) == "QUIT":
                terminate()
            DISPLAY_SURF.blit(original_surf, (0,0))
            pygame.display.update()
            last_input = qr.data
            return False
        if [scouter, team, match] in previous_inputs:
##            other_versions = [[scouter, team, match]]
##            duplicates = []
##            with open("duplicate_qr_strings.txt", 'r') as in_file:
##                duplicates = in_file.readlines()
##                version = [scouter + '*', team, match]
##                star_count = 1
##                while version in duplicates:
##                    star_count += 1
##                    version[0] += '*'
                
            original_surf = DISPLAY_SURF.copy()
            popup_window = Popup("Duplicate QR Code",
                                 "A qr code with this scouter, match#, and " +
                                 "team# has already been scanned. This code " +
                                 "contains data that is different from that " +
                                 "one. Are you sure you want to scan this " +
                                 "code? There will be a '*' appended to the " +
                                 "scouter name.", buttons="o/c",
                                 afirmative_button_text="SCAN", size=(350,230),
                                 pos=(DISPLAY_WIDTH//2, DISPLAY_HEIGHT//2),
                                 pos_type="center")
            scanned = popup_window.show(DISPLAY_SURF, FPS)
            if scanned == "QUIT":
                terminate()
            DISPLAY_SURF.blit(original_surf, (0,0))
            pygame.display.update()
            last_input = qr.data
            if not scanned:
                last_input = qr.data
                return scanned
            
            scouter += '*'
            iAr[0] += '*'
            qr.data = ""
            for item in iAr:
                qr.data += item + ','
            with open("duplicate_qr_strings.txt", 'a+') as out_file:
                out_file.write(qr.data + "\n")
            skip_match_and_team_check = True
        dstfile = "QRCodes\\"+team+"_"+match+".png"
        if not skip_match_and_team_check:
            for qr_data in previous_inputs:
                if qr_data[1:] == [team, match]:
                    original_surf = DISPLAY_SURF.copy()
                    popup_window = Popup("Duplicate QR Code",
                                         "This match number and team number " +
                                         "have already been scanned. However " +
                                         "the scouter name is " +
                                         "different. Do you want to scan " +
                                         "this one too?", buttons="o/c",
                                         afirmative_button_text="SCAN",
                                         pos=(DISPLAY_WIDTH//2, DISPLAY_HEIGHT//2),
                                         pos_type="center")
                    scanned = popup_window.show(DISPLAY_SURF, FPS)
                    if scanned == "QUIT":
                        terminate()
                    DISPLAY_SURF.blit(original_surf, (0,0))
                    pygame.display.update()
                    if not scanned:
                        last_input = qr.data
                        return scanned
                    break
        copyfile(file, dstfile)
        with open(QR_STRINGS, 'a+') as out_file:
            out_file.write(qr.data + "\n")
        with open(SETUP_LIST, 'ab+') as csvfile:
            csv_write = csv.writer(csvfile, dialect='excel', delimiter=',')
            csv_write.writerow(iAr[:num_match + num_setup])
        with open(EVENT_LIST, 'ab+') as csvfile:
            csv_write = csv.writer(csvfile, dialect='excel', delimiter=',')
            setup_arr = [team, match, "Game Phase"]
            csv_write.writerow(iAr[:num_match] + iAr[-num_vals:])

        if not skip_match_and_team_check:
            last_input = qr.data
        previous_inputs_full.append(qr.data)
        previous_inputs.append([scouter, team, match])
        return scanned
    return None

def flash_rect(rect, color, animation_speed=50, rect_on_top=None):
    original_surf = DISPLAY_SURF.copy()
    overlay = pygame.Surface((rect.width, rect.height))
    overlay = overlay.convert_alpha()
    r, g, b = color
    
    for start, end, step in ((0, 255, 1), (255, 0, -1)):
        for alpha in range(start, end, animation_speed*step):
            check_for_quit()
            DISPLAY_SURF.blit(original_surf, (0,0))
            overlay.fill((r, g, b, alpha))
            DISPLAY_SURF.blit(overlay, rect.topleft)
            if rect_on_top != None:
                DISPLAY_SURF.blit(rect_on_top[0], rect_on_top[1])
            pygame.display.update()
            FPS_CLOCK.tick(FPS)
    DISPLAY_SURF.blit(original_surf, (0,0))

def splash_screen():

    start_timer = 3
    
    while True:
        DISPLAY_SURF.fill(BLACK)

        if start_timer != -1:
            if start_timer <= 0:
                current_screen = SCAN_SCREEN
                return
            else:
                start_timer -= 1.0/FPS
        
        check_for_quit()

        pygame.display.update()
        FPS_CLOCK.tick(FPS)

def show_webcam():
    global text_boxes, focus, mouse_clicked, mouse_pos, next_match_button, \
           match_number_lable, match_number_textbox, match_number
    DISPLAY_SURF.fill(WHITE)

    ret_val, img = cam.read()
    cv2.imwrite(file, img)
    view_1 = pygame.image.load(file)
    view_1_rect = pygame.Rect((0, 0), view_1.get_size())
    view_1_rect.topleft = (100, 10)
    DISPLAY_SURF.blit(view_1, view_1_rect.topleft)

    num_previous_inputs = len(previous_inputs)
    top_pos = DISPLAY_HEIGHT - 25
    if num_previous_inputs > 7:
        for qr_data in previous_inputs[:num_previous_inputs-8:-1]:
            qr_data = "Saved - "+qr_data[0]+" : "+qr_data[1]+" : "+qr_data[2]
            draw_string(DISPLAY_SURF, qr_data, (view_1_rect.centerx, top_pos),
                        BLACK, size=15, pos_type="midtop")
            top_pos -= 20
    else:
        for qr_data in previous_inputs[::-1]:
            qr_data = "Saved - "+qr_data[0]+" : "+qr_data[1]+" : "+qr_data[2]
            draw_string(DISPLAY_SURF, qr_data, (view_1_rect.centerx, top_pos),
                        BLACK, size=15, pos_type="midtop")
            top_pos -= 20

    for tb in text_boxes:
        tb[0].render(DISPLAY_SURF)

    match_number_lable.render(DISPLAY_SURF)
    match_number_textbox.render(DISPLAY_SURF)

    pygame.draw.rect(DISPLAY_SURF, RED, next_match_button)
    draw_string(DISPLAY_SURF, "Setup Next Match", next_match_button.center,
                size=15, pos_type="center")

    scanned = decode_qr_code()
    if scanned == True:
        flash_rect(view_1_rect, GREEN)
    elif scanned == False:
        flash_rect(view_1_rect, RED)

    if mouse_clicked:
        for tb in text_boxes:
            if tb[0].hitbox.collidepoint(mouse_pos):
                focus = tb[1]
                break
        else:
            focus = -1
        if next_match_button.collidepoint(mouse_pos):
            for tb in text_boxes:
                tb[0].set_bg_color(RED)
            match_number += 1
            match_number_textbox.set_text(str(match_number))
        mouse_clicked = False
        if match_number_textbox.hitbox.collidepoint(mouse_pos):
            exit_code = match_number_textbox.user_input(DISPLAY_SURF)
            try:
                match_number = int(match_number_textbox.get_text())
            except:
                match_number_textbox.set_text(str(match_number))
            if exit_code[0] == "MOUSEBUTTONDOWN":
                mouse_clicked = True
                mouse_pos = exit_code[1]

    if focus != -1:
        for tb in text_boxes:
            if focus in tb:
                exit_code = tb[0].user_input(DISPLAY_SURF)
                if exit_code[0] == "MOUSEBUTTONDOWN":
                    mouse_clicked = True
                    mouse_pos = exit_code[1]
                elif exit_code[0] == "TAB":
                    focus += 1
                    if focus >= len(text_boxes):
                        focus = 0
                else:
                    focus = -1
                break

def check_for_quit():
    for event in pygame.event.get():
        if event.type == QUIT:
            terminate()

def main():
    global current_screen, text_boxes, focus, mouse_clicked, mouse_pos

    # splash_screen()
    setup_previous_inputs()
    setup_webcam_screen()

    while True:
        for event in pygame.event.get():
            if event.type == QUIT or \
               (event.type==KEYDOWN and event.key==285 and event.mod==256):
                terminate()
            elif event.type == MOUSEBUTTONDOWN:
                mouse_clicked = True
                mouse_pos = event.pos

        show_webcam()

        pygame.display.update()
        FPS_CLOCK.tick(FPS)

if __name__ == "__main__":
    main()
