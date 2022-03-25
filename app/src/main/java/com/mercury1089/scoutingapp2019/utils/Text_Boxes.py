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


def main():
    surf = pygame.display.set_mode((800,600))
    
    text_boxes = []
    top_pos = 100

    for i in range(6):
        text_boxes.append([Text_Box((100,top_pos)), i])
        top_pos += 30

    for tb in text_boxes:
        tb[0].set_bg_color(RED)

    mouse_clicked = False
    mouse_pos = (0,0)
    focus = -1

    text_boxes[0][0].set_hint("Hello, World!")
    
    while True:
        surf.fill((255,255,255))
        for tb in text_boxes:
            tb[0].render(surf)
        for event in pygame.event.get():
            if event.type == QUIT:
                pygame.quit()
                sys.exit()
            if event.type == MOUSEBUTTONDOWN:
                mouse_clicked = True
                mouse_pos = event.pos

        if mouse_clicked:
            for tb in text_boxes:
                if tb[0].hitbox.collidepoint(mouse_pos):
                    focus = tb[1]
                    break
            else:
                focus = -1
            mouse_clicked = False

        if focus != -1:
            for tb in text_boxes:
                if focus in tb:
                    exit_code = tb[0].user_input(surf)
                    tb[0].set_bg_color(GREEN)
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

        pygame.display.update()


if __name__ == "__main__":
    main()
