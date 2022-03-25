"""
Henry Rich

02/27/2020

Popup class
"""


import pygame, sys
from pygame.locals import *


pygame.init()
FPS_CLOCK = pygame.time.Clock()


BLACK = (  0,   0,   0)


BUTTON_TEXTS = {"y/n":["YES", "NO"], "o/c":["OK", "CANCEL"], "ok":["OK", ""]}


def draw_string(surface, string, pos, color=BLACK, size=10, pos_type="midtop"):
    font_obj = pygame.font.Font("freesansbold.ttf", size)
    text_surface_obj = font_obj.render(string, True, color)
    text_rect_obj = text_surface_obj.get_rect()
    if pos_type == "midtop":
        text_rect_obj.midtop = pos
    elif pos_type == "center":
        text_rect_obj.center = pos
    surface.blit(text_surface_obj, text_rect_obj)


def check_for_quit():
    for event in pygame.event.get():
        if event.type == KEYUP and event.key == K_ESCAPE:
            return True
    return False


class Popup:
    def __init__(self, title, message, buttons="y/n", afirmative_button_text=None,
                 negative_button_text=None, pos=(0,0), size=(350,200),
                 pos_type="topleft"):
        self.title = title
        self.message = message
        self.buttons = buttons
        self.size = size
        self.width, self.height = size
        self.x = None
        self.y = None
        if pos_type == "topleft":
            self.setX(pos[0])
            self.setY(pos[1])
        elif pos_type == "center":
            self.setX(pos[0]-self.width//2)
            self.setY(pos[1]-self.height//2)
        if afirmative_button_text == None:
            self.afirmative_button_text = BUTTON_TEXTS[buttons][0]
        else:
            self.afirmative_button_text = afirmative_button_text
        if negative_button_text == None:
            self.negative_button_text = BUTTON_TEXTS[buttons][1]
        else:
            self.negative_button_text = negative_button_text
        self.create()

    def setX(self, value):
        self.x = value
        self.pos = (self.x, self.y)

    def setY(self, value):
        self.y = value
        self.pos = (self.x, self.y)

    def create(self):
        self.surf = pygame.Surface(self.size)
        self.surf.fill((255,255,255))

        self.border = pygame.Rect(self.x-3, self.y-3, self.width+6,
                                  self.height+6)
        
        title_bar = pygame.Rect(0, 0, self.width, 30)
        pygame.draw.rect(self.surf, (0,0,255), title_bar)
        draw_string(self.surf, self.title, (self.width//2, 5), size=20)

        lines = []
        while len(self.message) > 0:
            line = self.message[:self.width//8]
            try:
                if len(self.message) > self.width//8:
                    last_space = line.rindex(' ')
                    line = self.message[:last_space]
                    self.message = self.message[last_space+1:]
                else:
                    self.message = ""
            except:
                try:
                    self.message = self.message[self.width//7+1:]
                except:
                    self.message = ""
            lines.append(line)

        top_pos = 40
        for line in lines:
            draw_string(self.surf, line, (self.width//2, top_pos), size=15)
            top_pos += 20

        standard_space = self.width//3

        if self.buttons != "ok":
            self.afirmative_button = pygame.Rect(standard_space//4,
                                                 self.height-50, standard_space,
                                                 30)
            pygame.draw.rect(self.surf, (255,0,0), self.afirmative_button)
            draw_string(self.surf, self.afirmative_button_text,
                        self.afirmative_button.center, size=20,
                        pos_type="center")

            self.negative_button = pygame.Rect(self.width//2+standard_space//4,
                                               self.height-50, standard_space,
                                               30)
            pygame.draw.rect(self.surf, (255,0,0), self.negative_button)
            draw_string(self.surf, self.negative_button_text,
                        self.negative_button.center, size=20,
                        pos_type="center")
        else:
            self.afirmative_button = pygame.Rect(standard_space,
                                                 self.height-50, standard_space,
                                                 30)
            pygame.draw.rect(self.surf, (255,0,0), self.afirmative_button)
            draw_string(self.surf, self.afirmative_button_text,
                        self.afirmative_button.center, size=20,
                        pos_type="center")

            self.negative_button = None

    def flash_border(self, surface, FPS, animation_speed=100):
        flash_surf = pygame.Surface(self.border.size)
        flash_surf = flash_surf.convert_alpha()

        r, g, b = (255, 255, 255)

        for start, end, step in ((0, 255, 1),
                                 (255, 0, -1)):
            for alpha in range(start, end, step*animation_speed):
                flash_surf.fill((r,g,b,alpha))
                pygame.draw.rect(surface, (0, 0, 255), self.border)
                surface.blit(flash_surf, self.border)
                surface.blit(self.surf, self.pos)
                if check_for_quit():
                    return "QUIT"
                pygame.display.update()
                FPS_CLOCK.tick(FPS)

    def show(self, surface, FPS):
        mouse_pos = (0,0)
        mouse_clicked = False

        border_hitbox = pygame.Rect(self.border)
        border_hitbox.x -= self.x
        border_hitbox.y -= self.y
        
        while True:
            pygame.draw.rect(surface, (0, 0, 255), self.border)
            surface.blit(self.surf, self.pos)
            for event in pygame.event.get():
                if event.type == KEYUP and event.key == K_ESCAPE:
                    return "QUIT"
                elif event.type == MOUSEBUTTONUP:
                    mouse_pos = (event.pos[0]-self.x, event.pos[1]-self.y)
                    mouse_clicked = True

            if mouse_clicked:
                if self.afirmative_button.collidepoint(mouse_pos):
                    return True
                elif self.negative_button != None and \
                     self.negative_button.collidepoint(mouse_pos):
                    return False
                elif not border_hitbox.collidepoint(mouse_pos):
                    for i in range(3):
                        if self.flash_border(surface, FPS) == "QUIT":
                            return "QUIT"
                mouse_clicked = False
            
            pygame.display.update()


def main():
    #global FPS_CLOCK
    
    DISPLAY_WIDTH, DISPLAY_HEIGHT = 800, 600
    DISPLAY_SURF = pygame.display.set_mode((DISPLAY_WIDTH, DISPLAY_HEIGHT))
    DISPLAY_SURF.fill(BLACK)
    FPS_CLOCK = pygame.time.Clock()
    pygame.display.set_caption("HI")
    popup_window = Popup("QR Code Already Scanned",
                                     "This match number and team number " +
                                     "have already been scanned. Do you " +
                                     "want to replace the previous code " +
                                     "with this one?", buttons="o/c",
                                     afirmative_button_text="SCAN",
                                     pos=(DISPLAY_WIDTH//2, DISPLAY_HEIGHT//2),
                                     pos_type="center")
    print(popup_window.show(DISPLAY_SURF, 30))
    pygame.quit()
    sys.exit()


if __name__ == "__main__":
    main()
