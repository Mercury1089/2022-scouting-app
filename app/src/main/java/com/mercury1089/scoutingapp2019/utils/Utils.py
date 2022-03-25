"""
Henry Rich

03/06/2020

Widgets
"""


import pygame, sys
from pygame.locals import *


pygame.init()


BLACK = (  0,   0,   0)
WHITE = (255, 255, 255)
RED   = (255,  50,  50)
GREEN = ( 50, 255,  50)
BLUE  = ( 50,  50, 255)
GREY  = ( 90,  90,  90)


def draw_string(surface, string, pos, color=BLACK, size=10, pos_type="midtop"):
    font_obj = pygame.font.SysFont("couriernew", size, True)
    text_width, text_height = font_obj.size('A')
    text_surface_obj = font_obj.render(string, True, color)
    text_rect_obj = text_surface_obj.get_rect()
    if pos_type == "midtop":
        text_rect_obj.midtop = pos
    elif pos_type == "center":
        text_rect_obj.center = pos
    else:
        text_rect_obj.topleft = pos
    surface.blit(text_surface_obj, text_rect_obj)
    return text_width


def check_for_quit():
    for event in pygame.event.get():
        if event.type == KEYUP and event.key == K_ESCAPE:
            return True
    return False


class Text_Box:
    def __init__(self, pos, size=(300,30), text="", hint="", bg_color=WHITE,
                 text_color=BLACK, selected_color=BLUE):
        self.pos = pos
        self.size = size
        self.hitbox = pygame.Rect(pos, size)
        self.text = text
        self.hint = hint
        self.x, self.y = pos
        self.width, self.height = size
        self.background_color = bg_color
        self.text_color = text_color
        self.selected_color = selected_color
        self.surface = pygame.Surface(self.size)
        self.selected = False
        self.text_width = 0
        self.curser = 0

    def set_text(self, text):
        self.text = text

    def get_text(self):
        return self.text

    def set_hint(self, text):
        self.hint = text

    def set_text_color(self, color):
        self.text_color = color

    def set_bg_color(self, color):
        self.background_color = color

    def repeat(self, key, clock):
        repeat_delay = (100, 5)
        if key[2] <= 0:
            if key[1]:
                key[2] = repeat_delay[0]
                key[1] = False
            else:
                key[2] = repeat_delay[1]
        else:
            key[2] -= clock.tick()
        return key

    def user_input(self, surf):
        original_surf = pygame.Surface.copy(surf)
        self.selected = True
        backspacing = [False, False, 0]
        deleting = [False, False, 0]
        moving_left = [False, False, 0]
        moving_right = [False, False, 0]
        clock = pygame.time.Clock()
        self.curser = len(self.text)
        while True:
            for event in pygame.event.get():
                if event.type == QUIT:
                    pygame.quit()
                    sys.exit()
                if event.type == MOUSEBUTTONDOWN:
                    if not self.hitbox.collidepoint(event.pos):
                        self.selected = False
                        return ("MOUSEBUTTONDOWN", event.pos)
                    else:
                        x = event.pos[0] - self.x - 9
                        self.curser = int((x + self.text_width/2) // \
                                          self.text_width)
                        if self.curser > len(self.text):
                            self.curser = len(self.text)
                
                elif event.type == KEYDOWN:
                    if event.key == 8: #backspace
                        backspacing = [True, True, 0]
                    elif event.key == 9: #tab
                        self.selected = False
                        return ("TAB", None)
                    elif event.key == 13 or event.key == 27: #return / esc
                        self.selected = False
                        return ("ESCAPE", None)
                    elif event.key == 127: #delete
                        deleting = [True, True, 0]
                    elif event.key == 276: #left arrow
                        moving_left = [True, True, 0]
                    elif event.key == 275: #right arrow
                        moving_right = [True, True, 0]
                    elif event.unicode != '': #other chars
                        self.text = self.text[:self.curser] + event.unicode + \
                                    self.text[self.curser:]
                        self.curser += 1
                elif event.type == KEYUP:
                    if event.key == 8: #backspace
                        backspacing = [False, False, 0]
                    elif event.key == 127: #delete
                        deleting = [False, False, 0]
                    elif event.key == 276: #left arrow
                        moving_left = [False, False, 0]
                    elif event.key == 275: #right arrow
                        moving_right = [False, False, 0]

            if backspacing[0] and self.curser > 0:
                if backspacing[2] <= 0:
                    self.text = self.text[:self.curser-1] + \
                                self.text[self.curser:]
                    self.curser -= 1
                backspacing = self.repeat(backspacing, clock)

            if deleting[0] and self.curser < len(self.text):
                if deleting[2] <= 0:
                    self.text = self.text[:self.curser] + \
                                self.text[self.curser+1:]
                deleting = self.repeat(deleting, clock)

            if moving_left[0] and self.curser > 0:
                if moving_left[2] <= 0:
                    self.curser -= 1
                moving_left = self.repeat(moving_left, clock)

            if moving_right[0] and self.curser < len(self.text):
                if moving_right[2] <= 0:
                    self.curser += 1
                moving_right = self.repeat(moving_right, clock)

            surf.blit(original_surf, (0,0))
            self.render(surf)
            self.draw_curser(surf)
            clock.tick()
            pygame.display.update()

    def draw_curser(self, surf):
        curser_x = self.curser * self.text_width + 9 + self.x
        if curser_x < self.x + self.width:
            pygame.draw.line(surf, BLACK, (curser_x,self.y+5),(curser_x,self.y+25))

    def build(self):
        self.surface = pygame.Surface(self.size)
        if self.selected:
            self.surface.fill(self.selected_color)
        else:
            self.surface.fill(self.background_color)

        top_border = pygame.Rect((0,0),(self.width,1))
        right_border = pygame.Rect((self.width-1,0),(1,self.height))
        bottom_border = pygame.Rect((0,self.height-1),(self.width,1))
        left_border = pygame.Rect((0,0),(1,self.height))

        pygame.draw.rect(self.surface, BLACK, top_border)
        pygame.draw.rect(self.surface, BLACK, right_border)
        pygame.draw.rect(self.surface, BLACK, bottom_border)
        pygame.draw.rect(self.surface, BLACK, left_border)

        if len(self.text) == 0 and len(self.hint) > 0:
            self.text_width = draw_string(self.surface, '<'+self.hint+'>',
                                          (10, 5), color=self.text_color,
                                          size=20, pos_type="topleft")
            return

        self.text_width = draw_string(self.surface, self.text, (10, 5),
                                      color=self.text_color, size=20,
                                      pos_type="topleft")
        

    def render(self, surf):
        self.build()
        surf.blit(self.surface, self.pos)


BUTTON_TEXTS = {"y/n":["YES", "NO"], "o/c":["OK", "CANCEL"], "ok":["OK", ""]}


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
                self.FPS_CLOCK.tick(FPS)

    def show(self, surface, FPS):
        mouse_pos = (0,0)
        mouse_clicked = False

        border_hitbox = pygame.Rect(self.border)
        border_hitbox.x -= self.x
        border_hitbox.y -= self.y

        self.FPS_CLOCK = pygame.time.Clock()
        
        while True:
            pygame.draw.rect(surface, (0, 0, 255), self.border)
            surface.blit(self.surf, self.pos)
            for event in pygame.event.get():
                if event.type == QUIT:
                    return "QUIT"
                elif event.type == MOUSEBUTTONDOWN:
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


class Button:
    def __init__(self, pos, size=(100,50), text="", bg_color=RED,
                 text_color=BLACK, return_value=[None]):
        self.pos = pos
        self.x, self.y = pos
        self.size = size
        self.width, self.height = size
        self.text = text
        self.background_color = bg_color
        self.text_color = text_color
        self.hitbox = pygame.Rect(pos, size)
        self.return_value = return_value

    def set_return_value(self, return_type, return_value, return_modifier=None):
        self.return_value = [return_type, return_value, return_modifiers]

    def check(self, mouse_pos):
        return self.hitbox.collidepoint(mouse_pos)

    def render(self, surf):
        pygame.draw.rect(surf, self.background_color, self.hitbox)
        draw_string(surf, self.text, self.hitbox.center, pos_type="center")

    def click(self):
        return self.return_value


class Custom_Popup(Popup):
    def __init__(self, title, message, buttons="y/n", afirmative_button_text=None,
                 negative_button_text=None, pos=(0,0), size=(350,200),
                 pos_type="topleft", return_value=[None]):
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

        self.items = []
        self.buttons = []
        self.text_boxes = []
        if return_value[0] == "TEXT":
            self.return_type = return_value[0]
            self.return_value = return_value[1]
        else:
            return_type = None
            return_value = True
    
    def add_item(self, surface, rel_pos):
        self.items.append([surface, rel_pos])

    def add_button(self, button):
        self.buttons.append(button)

    def add_textbox(self, tb, editable=False, tag=""):
        self.text_boxes.append([tb, editable, tag])

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

        for item, pos in self.items:
            self.surf.blit(item, pos)

        for button in self.buttons:
            button.render(self.surf)

        for tb, editable, tag in self.text_boxes:
            tb.render(self.surf)
        
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

    def show(self, surface, FPS):
        mouse_pos = (0,0)
        mouse_clicked = False

        self.create()

        border_hitbox = pygame.Rect(self.border)
        border_hitbox.x -= self.x
        border_hitbox.y -= self.y

        self.FPS_CLOCK = pygame.time.Clock()
        
        while True:
            pygame.draw.rect(surface, (0, 0, 255), self.border)
            surface.blit(self.surf, self.pos)
            for event in pygame.event.get():
                if event.type == QUIT:
                    return "QUIT"
                elif event.type == MOUSEBUTTONDOWN:
                    mouse_pos = (event.pos[0]-self.x, event.pos[1]-self.y)
                    mouse_clicked = True

            if mouse_clicked:
                if self.afirmative_button.collidepoint(mouse_pos):
                    if self.return_type == "TEXT":
                        for tb, editable, tag in self.text_boxes:
                            if tag == self.return_value:
                                return tb.get_text()
                        else:
                            raise SyntaxError("Tag not found")
                    else:
                        return True
                elif self.negative_button != None and \
                     self.negative_button.collidepoint(mouse_pos):
                    return False
                elif not border_hitbox.collidepoint(mouse_pos):
                    for i in range(3):
                        if self.flash_border(surface, FPS) == "QUIT":
                            return "QUIT"
                else:
                    for button in self.buttons:
                        if button.check(mouse_pos):
                            return_type, return_value, return_modifier \
                                         = button.click()
                            if return_type == "SET_TEXT":
                                for tb, editable, tag in self.text_boxes:
                                    if tag == return_value:
                                        tb.set_text(return_modifier)
                                        break
                                else:
                                    continue
                                break
                    for tb, editable, tag in self.text_boxes:
                        if editable:
                            tb.user_input(self.surf)
                mouse_clicked = False
            
            pygame.display.update()


def main():
    surf = pygame.display.set_mode((800,600))
    custom_popup = Custom_Popup("Unexpected Scouter Name", "The scouter name " +
                                "in the qr string doesn't match any of the " +
                                "expected scouter names. Press OK to use the " +
                                "scouter name that is in the textbox. Click " +
                                "on a preset name to auto-fill.", "ok",
                                pos=(400, 300), size=(600,500),
                                pos_type="center", return_value=["TEXT","t1"])
    custom_popup.add_button(Button((100,200), size=(100,50), text="HI",
                                   return_value=["SET_TEXT", "t1", "F"]))
    custom_popup.add_textbox(Text_Box((100,100), text="1"), True, "t1")
    print(custom_popup.show(surf, 30))


if __name__ == "__main__":
    main()
