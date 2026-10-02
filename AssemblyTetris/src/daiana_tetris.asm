.586
.model flat, stdcall
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;includem biblioteci, si declaram ce functii vrem sa importam
includelib msvcrt.lib
extern exit: proc
extern malloc: proc
extern memset: proc
extern printf:proc
includelib canvas.lib
extern BeginDrawing: proc
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;declaram simbolul start ca public - de acolo incepe executia
public start
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;sectiunile programului, date, respectiv cod
.data
;aici declaram date
window_title DB "TETRIS",0
area_width EQU 800
area_height EQU 700
area DD 0
format db " ",0
counter DD 0 ; numara evenimentele de tip timer
scor DD 0


rotire DD 0
switch dd 0
switch_1 DD 0
valoare dd 32
coordonata_y dd 300
coordonata_x dd 600

arg1 EQU 8
arg2 EQU 12
arg3 EQU 16
arg4 EQU 20
arg5 equ 24
; arg1 - simbolul de afisat (litera sau cifra)
; arg2 - pointer la vectorul de pixeli
; arg3 - pos_x
; arg4 - pos_y

symbol_width EQU 10
symbol_height EQU 20
galben_width EQU 15
galben_height EQU 15
square_width EQU 30
square_height EQU 30
linie_height equ 15
linie_width equ 60



cordXpiece dd 290
cordYpiece dd 125



include inc/digits.inc
include inc/letters.inc
include inc/square.inc
include inc/linie.inc
include inc/culori.inc
include patratel_alb.inc

;coordonate pt dreptunghiul albastru
button_x EQU 170
button_y EQU 125

button_size1 EQU 300 ;latime dreptunghi
button_size2 EQU 480  ;inaltime dreptunghi

tip0 EQU 0
tip1 EQU 1
tip2 EQU 2

lungime_patrat_mic equ 15
pozitie_x dd 280
pozitie_y dd 11

.code


; procedura make_text afiseaza o litera sau o cifra la coordonatele date
make_text proc

	push ebp
	mov ebp, esp
	pusha
	
	mov eax, [ebp+arg1] ; citim simbolul de afisat
	cmp eax, 'A'
	jl make_digit
	cmp eax, 'Z'
	jg make_digit
	sub eax, 'A'
	lea esi, letters
	jmp draw_text
make_digit:
	cmp eax, '0'
	jl make_space
	cmp eax, '9'
	jg make_space
	sub eax, '0'
	lea esi, digits
	jmp draw_text
make_space:	
	mov eax, 26 ; de la 0 pana la 25 sunt litere, 26 e space
	lea esi, letters
	
draw_text:
	mov ebx, symbol_width
	mul ebx
	mov ebx, symbol_height
	mul ebx
	add esi, eax
	mov ecx, symbol_height
bucla_simbol_linii:
	mov edi, [ebp+arg2] ; pointer la matricea de pixeli
	mov eax, [ebp+arg4] ; pointer la coord y
	add eax, symbol_height
	sub eax, ecx
	mov ebx, area_width
	mul ebx
	add eax, [ebp+arg3] ; pointer la coord x
	shl eax, 2 ; inmultim cu 4, avem un DWORD per pixel
	add edi, eax
	push ecx
	mov ecx, symbol_width
bucla_simbol_coloane:
	cmp byte ptr [esi], 0
	je simbol_pixel_alb
	mov dword ptr [edi], 0
	jmp simbol_pixel_next
simbol_pixel_alb:
	mov dword ptr [edi], 0FFFFFFh
simbol_pixel_next:
	inc esi
	add edi, 4
	loop bucla_simbol_coloane
	pop ecx
	loop bucla_simbol_linii
	popa
	mov esp, ebp
	pop ebp
	ret
make_text endp

; un macro ca sa apelam mai usor desenarea simbolului
make_text_macro macro symbol, drawArea, x, y
	push y
	push x
	push drawArea
	push symbol
	call make_text
	add esp, 16
endm
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


; make_text2 proc
	; push ebp
	; mov ebp, esp
	; pusha

	; lea esi, patratul_0
	
	
; draw_image:
	; mov ecx, square_height
; loop_draw_lines:
	; mov edi, [ebp+arg1] ; pointer to pixel area
	; mov eax, [ebp+arg3] ; pointer to coordinate y
	
	; add eax, square_height 
	; sub eax, ecx ; current line to draw (total - ecx)
	
	; mov ebx, area_width
	; mul ebx	; get to current line
	
	; add eax, [ebp+arg2] ; get to coordinate x in current line
	; shl eax, 2 ; multiply by 4 (DWORD per pixel)
	; add edi, eax
	
	; push ecx
	; mov ecx, square_width ; store drawing width for drawing loop
	
; loop_draw_columns:

	; push eax
	; mov eax, dword ptr[esi] 
	; mov dword ptr [edi], eax ; take data from variable to canvas
	; pop eax
	
	; add esi, 4
	; add edi, 4 ; next dword (4 Bytes)
	
	; loop loop_draw_columns
	
	; pop ecx
	; loop loop_draw_lines
	; popa
	
	; mov esp, ebp
	; pop ebp

	; ret
; make_text2 endp

; simple macro to call the procedure easier
; make_image_macro macro drawArea, x, y
	; push y
	; push x
	; push drawArea
	; call make_text2
	; add esp, 12
; endm

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; make_text3 proc
	; push ebp
	; mov ebp, esp
	; pusha

	; lea esi, linie_0
	
; draw_image:
	; mov ecx, linie_height
; loop_draw_lines:
	; mov edi, [ebp+arg1] ; pointer to pixel area
	; mov eax, [ebp+arg3] ; pointer to coordinate y
	
	; add eax, linie_height 
	; sub eax, ecx ; current line to draw (total - ecx)
	
	; mov ebx, area_width
	; mul ebx	; get to current line
	
	; add eax, [ebp+arg2] ; get to coordinate x in current line
	; shl eax, 2 ; multiply by 4 (DWORD per pixel)
	; add edi, eax
	
	; push ecx
	; mov ecx, linie_width ; store drawing width for drawing loop
	
; loop_draw_columns:

	; push eax
	; mov eax, dword ptr[esi] 
	; mov dword ptr [edi], eax ; take data from variable to canvas
	; pop eax
	
	; add esi, 4
	; add edi, 4 ; next dword (4 Bytes)
	
	; loop loop_draw_columns
	
	; pop ecx
	; loop loop_draw_lines
	; popa
	
	; mov esp, ebp
	; pop ebp
	; ret
; make_text3 endp

; simple macro to call the procedure easier
; make_image_macro2 macro drawArea, x, y
	; push y
	; push x
	; push drawArea
	; call make_text3
	; add esp, 12
; endm

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
linie_horizontal macro x, y,len, color
local bucla_linie
   mov eax, y; EAX=y
   mov ebx, area_width
   mul ebx; EAX=y*area_width
   add eax,x; EAX=y*area_width+x
   shl eax,2; EAX=(y*area_width+x)*4
   add eax,area
   mov ecx,len
  bucla_linie:
  mov dword ptr[eax], color
  add eax,4
  loop bucla_linie
 endm
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 
 linie_vertical macro x, y,len, color
local bucla_linie
   mov eax, y; EAX=y
   mov ebx, area_width
   mul ebx; EAX=y*area_width
   add eax,x; EAX=y*area_width+x
   shl eax,2; EAX=(y*area_width+x)*4
   add eax,area
   mov ecx,len
  bucla_linie:
  mov dword ptr[eax], color
  add eax,area_width*4
  loop bucla_linie
 endm 
 
 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
 
make_patrat_mic_0 proc

	push ebp
	mov ebp, esp
	pusha

	cmp dword ptr[ebp+arg4], 0
	jne next
	lea esi,rosu
	jmp draw_image
	next:
	cmp dword ptr [ebp+arg4], 1
	jne next1
	lea esi,verde
	jmp draw_image
	next1:
	cmp dword ptr [ebp+arg4], 2
	jne next2
	lea esi,mov_i
	jmp draw_image
	next2:
	cmp dword ptr [ebp+arg4], 3
	jne next3
	lea esi,albastru_deschis
	jmp draw_image
	next3:
	cmp dword ptr [ebp+arg4], 4
	jne next4
	lea esi,albastru_inchis
	jmp draw_image
	next4:
	cmp dword ptr [ebp+arg4], 5
	jne next5
	lea esi,roz
	jmp draw_image
	next5:
	cmp dword ptr [ebp+arg4], 6
	jne next6
    lea esi,portocaliu
	jmp draw_image
	next6:

draw_image:
	mov ecx, lungime_patrat_mic
loop_draw_lines:
	mov edi, [ebp+arg1] ; pointer to pixel area
	mov eax, [ebp+arg3] ; pointer to coordinate y
	
	add eax, lungime_patrat_mic
	sub eax, ecx ; current line to draw (total - ecx)
	
	mov ebx, area_width
	mul ebx	; get to current line
	
	add eax, [ebp+arg2] ; get to coordinate x in current line
	shl eax, 2 ; multiply by 4 (DWORD per pixel)
	add edi, eax
	
	push ecx
	mov ecx, lungime_patrat_mic ; store drawing width for drawing loop
	
loop_draw_columns:

	push eax
	mov eax, dword ptr[esi] 
	mov dword ptr [edi], eax ; take data from variable to canvas
	pop eax
	
	add esi, 4
	add edi, 4 ; next dword (4 Bytes)
	
	loop loop_draw_columns
	
	pop ecx
	loop loop_draw_lines
	popa
	
	mov esp, ebp
	pop ebp
	ret
make_patrat_mic_0 endp

;simple macro to call the procedure easier
make_patrat_mic_macro_0 macro drawArea, x, y,nr_color

    push nr_color
	push y
	push x
	push drawArea
	call make_patrat_mic_0
	add esp, 16
endm  



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;zona pt facut piese;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

piesa_patrat MACRO darea,x,y

   pusha
   make_patrat_mic_macro_0 darea, x, y, 4
   push x
    
   mov ebx,x
   add ebx,lungime_patrat_mic
   mov x,ebx
   
   make_patrat_mic_macro_0 darea, x, y, 4
   pop x
   
   push y
   
   mov eax,y
   add eax,lungime_patrat_mic
   mov y,eax
   make_patrat_mic_macro_0 darea, x, y, 4
   
   push x
   
   mov ebx,x
   add ebx,lungime_patrat_mic
   mov x,ebx
   make_patrat_mic_macro_0 darea, x, y, 4
   pop x
   pop y
      	
    popa

endm
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

piesa_linie MACRO darea,x,y
   pusha
   push y
   
   make_patrat_mic_macro_0 darea, x, y,3    
   
   mov eax, y
   add eax,lungime_patrat_mic
   mov y,eax
   make_patrat_mic_macro_0 darea, x, y,3
   
   mov eax, y
   add eax,lungime_patrat_mic
   mov y,eax
   make_patrat_mic_macro_0 darea, x, y,3
   
   mov eax, y
   add eax,lungime_patrat_mic
   mov y,eax
   make_patrat_mic_macro_0 darea, x, y,3
   
   
   pop y
   popa

endm
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

piesa_linie_orizontala MACRO darea,x,y
   pusha
   push x
   
   make_patrat_mic_macro_0 darea, x, y,3  ;se poate apela o data dar nu de mai multe ori
   
   mov eax, x
   add eax,lungime_patrat_mic
   mov x,eax
   make_patrat_mic_macro_0 darea, x, y,3
   
   mov eax, x
   add eax,lungime_patrat_mic
   mov x,eax
   make_patrat_mic_macro_0 darea, x, y,3
   
   mov eax, x
   add eax,lungime_patrat_mic
   mov x,eax
   make_patrat_mic_macro_0 darea, x, y,3
   
   
   pop x
   popa

endm
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
piesa_L_stanga MACRO darea,x,y
   pusha
   push y
   
   make_patrat_mic_macro_0 darea, x, y,6    
   
   mov eax, y
   add eax,lungime_patrat_mic
   mov y,eax
   make_patrat_mic_macro_0 darea, x, y,6
   
   
   add eax,lungime_patrat_mic
   mov y,eax
   make_patrat_mic_macro_0 darea, x, y,6
   
   push x
   mov eax, x
   sub eax,lungime_patrat_mic
   mov x,eax
   make_patrat_mic_macro_0 darea, x, y,6
   
   
   pop x
   pop y
   popa

endm
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
piesa_L_stanga_sus MACRO darea,x,y
   pusha
   push y
   
   make_patrat_mic_macro_0 darea, x, y,6   
   mov eax, y
   add eax,lungime_patrat_mic
   mov y,eax
  make_patrat_mic_macro_0 darea, x, y,6
   
   
   add eax,lungime_patrat_mic
   mov y,eax
   make_patrat_mic_macro_0 darea, x, y,6
   pop y
     push x
   mov eax, x
   add eax,lungime_patrat_mic
   mov x,eax
   make_patrat_mic_macro_0 darea, x, y,6
   pop x
   
   popa

endm
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

piesa_L_stanga_culcat_sus MACRO darea,x,y
   pusha
   push y
    
    make_patrat_mic_macro_0 darea, x, y,6
	
   mov eax, y
   add eax,lungime_patrat_mic
   mov y,eax
  make_patrat_mic_macro_0 darea, x, y,6
   
   push x
   mov eax,x
   add eax,lungime_patrat_mic
   mov x,eax
   make_patrat_mic_macro_0 darea, x, y,6
   
   mov eax,x
   add eax,lungime_patrat_mic
   mov x,eax
   make_patrat_mic_macro_0 darea, x, y,6
   
   pop x
   pop y
   popa

endm
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

piesa_L_stanga_culcat_jos MACRO darea,x,y
   pusha
   push y
    
    make_patrat_mic_macro_0 darea, x, y,6
	
   mov eax, y
   sub eax,lungime_patrat_mic
   mov y,eax
   make_patrat_mic_macro_0 darea, x, y,6
   
   push x
   mov eax,x
   sub eax,lungime_patrat_mic
   mov x,eax
   make_patrat_mic_macro_0 darea, x, y,6
   
   
   mov eax,x
   sub eax,lungime_patrat_mic
   mov x,eax
   make_patrat_mic_macro_0 darea, x, y,6
   
   pop x
   pop y
   popa

endm
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
piesa_L_dreapta_sus MACRO darea,x,y
   
   pusha
   push y
   
   make_patrat_mic_macro_0 darea, x, y,1
   
   mov eax, y
   sub eax,lungime_patrat_mic
   mov y,eax
    make_patrat_mic_macro_0 darea, x, y,1
   
   mov eax,y
   sub eax,lungime_patrat_mic
   mov y,eax
  make_patrat_mic_macro_0 darea, x, y,1
   
   push x
   mov eax, x
   sub eax,lungime_patrat_mic
   mov x,eax
   make_patrat_mic_macro_0 darea, x, y,1
   
   
   pop x
   pop y
   popa

endm
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

piesa_L_dreapta_culcat_sus MACRO darea,x,y
   pusha
   push y
   
    make_patrat_mic_macro_0 darea, x, y,1
   
   mov eax, y
   sub eax,lungime_patrat_mic
   mov y,eax
    make_patrat_mic_macro_0 darea, x, y,1
   
   push x
   mov eax,x
   sub eax,lungime_patrat_mic
   mov x,eax
    make_patrat_mic_macro_0 darea, x, y,1
   

   mov eax, x
   sub eax,lungime_patrat_mic
   mov x,eax
    make_patrat_mic_macro_0 darea, x, y,1
   
   
   pop x
   pop y
   popa

endm

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
piesa_L_dreapta_culcat_jos MACRO darea,x,y
   pusha
   push y
   
    make_patrat_mic_macro_0 darea, x, y,1   
   
   mov eax, y
   add eax,lungime_patrat_mic
   mov y,eax
   make_patrat_mic_macro_0 darea, x, y,1
   
   push x
   mov eax,x
   add eax,lungime_patrat_mic
   mov x,eax
    make_patrat_mic_macro_0 darea, x, y,1
   

   mov eax, x
   add eax,lungime_patrat_mic
   mov x,eax
    make_patrat_mic_macro_0 darea, x, y,1
   
   pop x
   pop y
   popa

endm

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
piesa_L_dreapta MACRO darea,x,y
   pusha
   push y
   
    make_patrat_mic_macro_0 darea, x, y,1
   
   mov eax, y
   add eax,lungime_patrat_mic
   mov y,eax
    make_patrat_mic_macro_0 darea, x, y,1
   
   
   add eax,lungime_patrat_mic
   mov y,eax
   make_patrat_mic_macro_0 darea, x, y,1
   
   push x
   mov eax, x
   add eax,lungime_patrat_mic
   mov x,eax
    make_patrat_mic_macro_0 darea, x, y,1
   
   
   pop x
   pop y
   popa

endm
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

piesa_Z_dreapta MACRO darea,x,y
  pusha
   push x
   
   make_patrat_mic_macro_0 darea, x, y,2    
   
   mov eax,x
   add eax,lungime_patrat_mic
   mov x,eax
   make_patrat_mic_macro_0 darea, x, y ,2
   
   push y
   mov eax,y
   sub eax,lungime_patrat_mic
   mov y,eax
   make_patrat_mic_macro_0 darea, x, y ,2

	
    mov eax,x
   add eax,lungime_patrat_mic
   mov x,eax
   make_patrat_mic_macro_0 darea, x, y,2 
   
   
    pop y
    pop x
	
    popa

endm
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


piesa_Z_dreapta_sus MACRO darea,x,y 
  pusha
   push y
   
   make_patrat_mic_macro_0 darea, x, y,2
   
   mov eax,y
   add eax,lungime_patrat_mic
   mov y,eax
   make_patrat_mic_macro_0 darea, x, y ,2
   
   push x
   mov eax,x
   add eax,lungime_patrat_mic
   mov x,eax
   make_patrat_mic_macro_0 darea, x, y ,2
   
   mov eax,y
   add eax,lungime_patrat_mic
   mov y,eax
   make_patrat_mic_macro_0 darea, x, y,2 
   
    pop x
   pop y

    popa

endm
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

piesa_Z_stanga MACRO darea,x,y 
  pusha
   push x
   
   make_patrat_mic_macro_0 darea, x, y,5   
   
   mov eax,x
   add eax,lungime_patrat_mic
   mov x,eax
   make_patrat_mic_macro_0 darea, x, y ,5
   
   push y
   mov eax,y
   add eax,lungime_patrat_mic
   mov y,eax
   make_patrat_mic_macro_0 darea, x, y,5 
   
   mov eax,x
   add eax,lungime_patrat_mic
   mov x,eax
   make_patrat_mic_macro_0 darea, x, y,5 
   
    pop y
   pop x

    popa

endm
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

piesa_Z_stanga_sus MACRO darea,x,y 
  pusha
   push y
   
  make_patrat_mic_macro_0 darea, x, y,5
   
   mov eax,y
   sub eax,lungime_patrat_mic
   mov y,eax
   make_patrat_mic_macro_0 darea, x, y ,5
   
   push x
   mov eax,x
   add eax,lungime_patrat_mic
   mov x,eax
   make_patrat_mic_macro_0 darea, x, y,5 
   
   mov eax,y
   sub eax,lungime_patrat_mic
   mov y,eax
   make_patrat_mic_macro_0 darea, x, y ,5
   
    pop x
   pop y

    popa

endm
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
piesa_T MACRO darea,x,y 
 pusha
   push x
   
   make_patrat_mic_macro_0 darea, x, y,0
   
   mov eax,x
   add eax,lungime_patrat_mic
   mov x,eax
   make_patrat_mic_macro_0 darea, x, y ,0
   
   push y
   mov eax,y
   sub eax,lungime_patrat_mic
   mov y,eax
   make_patrat_mic_macro_0 darea, x, y ,0
    pop y
	
	
    mov eax,x
   add eax,lungime_patrat_mic
   mov x,eax
   make_patrat_mic_macro_0 darea, x, y ,0
   
    pop x

    popa

endm
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
piesa_T_jos MACRO darea,x,y 
 pusha
   push x
   
   make_patrat_mic_macro_0 darea, x, y,0    
   
   mov eax,x
   add eax,lungime_patrat_mic
   mov x,eax
   make_patrat_mic_macro_0 darea, x, y,0 
   
   push y
   mov eax,y
   add eax,lungime_patrat_mic
   mov y,eax
   make_patrat_mic_macro_0 darea, x, y ,0
    pop y
	
    mov eax,x
   add eax,lungime_patrat_mic
   mov x,eax
   make_patrat_mic_macro_0 darea, x, y ,0
   
    pop x
    popa

endm
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

piesa_T_stanga MACRO darea,x,y 
 pusha
   push y
   
  make_patrat_mic_macro_0 darea, x, y,0    
   
   mov eax,y
   add eax,lungime_patrat_mic
   mov y,eax
   make_patrat_mic_macro_0 darea, x, y ,0
   
   push x
   mov eax,x
   sub eax,lungime_patrat_mic
   mov x,eax
   make_patrat_mic_macro_0 darea, x, y ,0
    pop x

    mov eax,y
   add eax,lungime_patrat_mic
   mov y,eax
  make_patrat_mic_macro_0 darea, x, y,0 
   
    pop y
    popa

endm
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

piesa_T_dreapta MACRO darea,x,y 
 pusha
   push y
   
   make_patrat_mic_macro_0 darea, x, y,0    
   
   mov eax,y
   add eax,lungime_patrat_mic
   mov y,eax
   make_patrat_mic_macro_0 darea, x, y ,0
   
   push x
   mov eax,x
   add eax,lungime_patrat_mic
   mov x,eax
   make_patrat_mic_macro_0 darea, x, y,0
    pop x
    mov eax,y
   add eax,lungime_patrat_mic
   mov y,eax
   make_patrat_mic_macro_0 darea, x, y ,0
   
    pop y

    popa

endm

;----------------------------------------------------------------------------------------------------------- zona pt miscare piese;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;penru piesa de patrat

miscare_patrat macro 
	
    local stanga, dreapta,iesire
    
    cmp cordYpiece, 575  ;pana unde are voie sa ajunga in jos
    jge iesire

    mov eax, [ebp+arg2]
    cmp eax, 27h
    je dreapta
    cmp eax, 25h
    je stanga

    add cordYpiece, 15
    jmp iesire

    stanga:
    cmp cordXpiece, 170  ;de unde incepe patratul sa se miste
    jle iesire
    sub cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    dreapta:
    cmp cordXpiece, 430  ;pana unde are voie sa ajunga
    jge iesire
    add cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    iesire:
    mov eax, area_width
    imul eax, area_height
    imul eax, 4
    push eax
    push 255
    push area
    call memset
    add esp, 12

    piesa_patrat area, cordXpiece, cordYpiece
	
endm
      
	  
; pt linie

miscare_linie macro
	
    local stanga, dreapta,iesire
    
    cmp cordYpiece, 540  ;pana unde are voie sa ajunga in jos
    jge iesire

    mov eax, [ebp+arg2]
    cmp eax, 27h
    je dreapta
    cmp eax, 25h
    je stanga

    add cordYpiece, 15
    jmp iesire

    stanga:
    cmp cordXpiece, 170  ;de unde incepe patratul sa se miste
    jle iesire
    sub cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    dreapta:
    cmp cordXpiece, 450  ;pana unde are voie sa ajunga
    jge iesire
    add cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    iesire:
    mov eax, area_width
    imul eax, area_height
    imul eax, 4
    push eax
    push 255
    push area
    call memset
    add esp, 12

    piesa_linie area, cordXpiece, cordYpiece
	
endm
     

;pt linie orizontala
miscare_linie_orz macro
	
    local stanga, dreapta,iesire
    
    cmp cordYpiece, 590  ;pana unde are voie sa ajunga in jos
    jge iesire

    mov eax, [ebp+arg2]
    cmp eax, 27h
    je dreapta
    cmp eax, 25h
    je stanga

    add cordYpiece, 15
    jmp iesire

    stanga:
    cmp cordXpiece, 170  ;de unde incepe patratul sa se miste
    jle iesire
    sub cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    dreapta:
    cmp cordXpiece, 400  ;pana unde are voie sa ajunga
    jge iesire
    add cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    iesire:
    mov eax, area_width
    imul eax, area_height
    imul eax, 4
    push eax
    push 255
    push area
    call memset
    add esp, 12

    piesa_linie_orizontala area, cordXpiece, cordYpiece
	
endm
     
	  
; piesa L stanga 

miscare_L_st macro
	
    local stanga, dreapta,iesire
    
    cmp cordYpiece, 560  ;pana unde are voie sa ajunga in jos
    jge iesire

    mov eax, [ebp+arg2]
    cmp eax, 27h
    je dreapta
    cmp eax, 25h
    je stanga

    add cordYpiece, 15
    jmp iesire

    stanga:
    cmp cordXpiece, 185  ;de unde incepe patratul sa se miste
    jle iesire
    sub cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    dreapta:
    cmp cordXpiece, 445  ;pana unde are voie sa ajunga
    jge iesire
    add cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    iesire:
    mov eax, area_width
    imul eax, area_height
    imul eax, 4
    push eax
    push 255
    push area
    call memset
    add esp, 12

    piesa_L_stanga area, cordXpiece, cordYpiece
	
endm
    
	
;piesa L stanga sus

 miscare_L_st_sus macro
	
    local stanga, dreapta,iesire
    
    cmp cordYpiece, 560  ;pana unde are voie sa ajunga in jos
    jge iesire

    mov eax, [ebp+arg2]
    cmp eax, 27h
    je dreapta
    cmp eax, 25h
    je stanga

    add cordYpiece, 15
    jmp iesire

    stanga:
    cmp cordXpiece, 170  ;de unde incepe patratul sa se miste
    jle iesire
    sub cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    dreapta:
    cmp cordXpiece, 430  ;pana unde are voie sa ajunga
    jge iesire
    add cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    iesire:
    mov eax, area_width
    imul eax, area_height
    imul eax, 4
    push eax
    push 255
    push area
    call memset
    add esp, 12

    piesa_L_stanga_sus area, cordXpiece, cordYpiece
	
endm
     
	  
;piesa L stanga culcat sus



 miscare_L_st_culcat_sus macro
	
    local stanga, dreapta,iesire
    
    cmp cordYpiece, 575  ;pana unde are voie sa ajunga in jos
    jge iesire

    mov eax, [ebp+arg2]
    cmp eax, 27h
    je dreapta
    cmp eax, 25h
    je stanga

    add cordYpiece, 15
    jmp iesire

    stanga:
    cmp cordXpiece, 170  ;de unde incepe patratul sa se miste
    jle iesire
    sub cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    dreapta:
    cmp cordXpiece, 415  ;pana unde are voie sa ajunga
    jge iesire
    add cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    iesire:
    mov eax, area_width
    imul eax, area_height
    imul eax, 4
    push eax
    push 255
    push area
    call memset
    add esp, 12

    piesa_L_stanga_culcat_sus area, cordXpiece, cordYpiece
	
endm
     

;piesa L stanga culcat jos

 miscare_L_st_culcat_jos macro
	
    local stanga, dreapta,iesire
    
    cmp cordYpiece, 590  ;pana unde are voie sa ajunga in jos
    jge iesire

    mov eax, [ebp+arg2]
    cmp eax, 27h
    je dreapta
    cmp eax, 25h
    je stanga

    add cordYpiece, 15
    jmp iesire

    stanga:
    cmp cordXpiece, 200  ;de unde incepe patratul sa se miste
    jle iesire
    sub cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    dreapta:
    cmp cordXpiece, 445  ;pana unde are voie sa ajunga
    jge iesire
    add cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    iesire:
    mov eax, area_width
    imul eax, area_height
    imul eax, 4
    push eax
    push 255
    push area
    call memset
    add esp, 12

    piesa_L_stanga_culcat_jos area, cordXpiece, cordYpiece
	
endm
     
	  
	  
;piesa L dreapta sus
 miscare_L_dr_sus macro
	
    local stanga, dreapta,iesire
    
    cmp cordYpiece, 590  ;pana unde are voie sa ajunga in jos
    jge iesire

    mov eax, [ebp+arg2]
    cmp eax, 27h
    je dreapta
    cmp eax, 25h
    je stanga

    add cordYpiece, 15
    jmp iesire

    stanga:
    cmp cordXpiece, 185  ;de unde incepe patratul sa se miste
    jle iesire
    sub cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    dreapta:
    cmp cordXpiece, 445  ;pana unde are voie sa ajunga
    jge iesire
    add cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    iesire:
    mov eax, area_width
    imul eax, area_height
    imul eax, 4
    push eax
    push 255
    push area
    call memset
    add esp, 12

    piesa_L_dreapta_sus area, cordXpiece, cordYpiece
	
endm
      
	  
; piesa L dreapta culcat sus
miscare_L_dr_culcat_sus macro


    local stanga, dreapta,iesire
    
    cmp cordYpiece, 590  ;pana unde are voie sa ajunga in jos
    jge iesire

    mov eax, [ebp+arg2]
    cmp eax, 27h
    je dreapta
    cmp eax, 25h
    je stanga

    add cordYpiece, 15
    jmp iesire

    stanga:
    cmp cordXpiece, 200  ;de unde incepe patratul sa se miste
    jle iesire
    sub cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    dreapta:
    cmp cordXpiece, 445  ;pana unde are voie sa ajunga
    jge iesire
    add cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    iesire:
    mov eax, area_width
    imul eax, area_height
    imul eax, 4
    push eax
    push 255
    push area
    call memset
    add esp, 12

   piesa_L_dreapta_culcat_sus area, cordXpiece, cordYpiece
	
endm
     
;piesa L dreapta culcat jos

miscare_L_dr_culcat_jos macro
	
    local stanga, dreapta,iesire
    
    cmp cordYpiece, 575  ;pana unde are voie sa ajunga in jos
    jge iesire

    mov eax, [ebp+arg2]
    cmp eax, 27h
    je dreapta
    cmp eax, 25h
    je stanga

    add cordYpiece, 15
    jmp iesire

    stanga:
    cmp cordXpiece, 170  ;de unde incepe patratul sa se miste
    jle iesire
    sub cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    dreapta:
    cmp cordXpiece, 415  ;pana unde are voie sa ajunga
    jge iesire
    add cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    iesire:
    mov eax, area_width
    imul eax, area_height
    imul eax, 4
    push eax
    push 255
    push area
    call memset
    add esp, 12

   piesa_L_dreapta_culcat_jos area, cordXpiece, cordYpiece
	
endm
     
; piesa L dreapta

 miscare_L_dr macro
	
    local stanga, dreapta,iesire
    
    cmp cordYpiece, 560  ;pana unde are voie sa ajunga in jos
    jge iesire

    mov eax, [ebp+arg2]
    cmp eax, 27h
    je dreapta
    cmp eax, 25h
    je stanga

    add cordYpiece, 15
    jmp iesire

    stanga:
    cmp cordXpiece, 170  ;de unde incepe patratul sa se miste
    jle iesire
    sub cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    dreapta:
    cmp cordXpiece, 430  ;pana unde are voie sa ajunga
    jge iesire
    add cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    iesire:
    mov eax, area_width
    imul eax, area_height
    imul eax, 4
    push eax
    push 255
    push area
    call memset
    add esp, 12

   piesa_L_dreapta area, cordXpiece, cordYpiece
	
endm
     
;piesa Z dreapta

 miscare_Z_dr macro
	
    local stanga, dreapta,iesire
    
    cmp cordYpiece, 590  ;pana unde are voie sa ajunga in jos
    jge iesire

    mov eax, [ebp+arg2]
    cmp eax, 27h
    je dreapta
    cmp eax, 25h
    je stanga

    add cordYpiece, 15
    jmp iesire

    stanga:
    cmp cordXpiece, 170  ;de unde incepe patratul sa se miste
    jle iesire
    sub cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    dreapta:
    cmp cordXpiece, 415  ;pana unde are voie sa ajunga
    jge iesire
    add cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    iesire:
    mov eax, area_width
    imul eax, area_height
    imul eax, 4
    push eax
    push 255
    push area
    call memset
    add esp, 12

   piesa_Z_dreapta area, cordXpiece, cordYpiece
	
endm
     
; piesa Z dreapta sus


 miscare_Z_dr_sus macro
	
    local stanga, dreapta,iesire
    
    cmp cordYpiece, 560  ;pana unde are voie sa ajunga in jos
    jge iesire

    mov eax, [ebp+arg2]
    cmp eax, 27h
    je dreapta
    cmp eax, 25h
    je stanga

    add cordYpiece, 15
    jmp iesire

    stanga:
    cmp cordXpiece, 170  ;de unde incepe patratul sa se miste
    jle iesire
    sub cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    dreapta:
    cmp cordXpiece, 430  ;pana unde are voie sa ajunga
    jge iesire
    add cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    iesire:
    mov eax, area_width
    imul eax, area_height
    imul eax, 4
    push eax
    push 255
    push area
    call memset
    add esp, 12

   piesa_Z_dreapta_sus area, cordXpiece, cordYpiece
	
endm
      

;piesa Z stanga 

	  
 miscare_Z_st macro
	
    local stanga, dreapta,iesire
    
    cmp cordYpiece, 575  ;pana unde are voie sa ajunga in jos
    jge iesire

    mov eax, [ebp+arg2]
    cmp eax, 27h
    je dreapta
    cmp eax, 25h
    je stanga

    add cordYpiece, 15
    jmp iesire

    stanga:
    cmp cordXpiece, 170  ;de unde incepe patratul sa se miste
    jle iesire
    sub cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    dreapta:
    cmp cordXpiece, 415  ;pana unde are voie sa ajunga
    jge iesire
    add cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    iesire:
    mov eax, area_width
    imul eax, area_height
    imul eax, 4
    push eax
    push 255
    push area
    call memset
    add esp, 12

   piesa_Z_stanga area, cordXpiece, cordYpiece
	
endm
     
	  
; piesa Z staga sus


miscare_Z_st_sus macro
	
    local stanga, dreapta,iesire
    
    cmp cordYpiece, 590  ;pana unde are voie sa ajunga in jos
    jge iesire

    mov eax, [ebp+arg2]
    cmp eax, 27h
    je dreapta
    cmp eax, 25h
    je stanga

    add cordYpiece, 15
    jmp iesire

    stanga:
    cmp cordXpiece, 170  ;de unde incepe patratul sa se miste
    jle iesire
    sub cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    dreapta:
    cmp cordXpiece, 430  ;pana unde are voie sa ajunga
    jge iesire
    add cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    iesire:
    mov eax, area_width
    imul eax, area_height
    imul eax, 4
    push eax
    push 255
    push area
    call memset
    add esp, 12

   piesa_Z_stanga_sus area, cordXpiece, cordYpiece
	
endm
     
; piesa t

 miscare_T_1 macro
	
    local stanga, dreapta,iesire
    
    cmp cordYpiece, 590  ;pana unde are voie sa ajunga in jos
    jge iesire

    mov eax, [ebp+arg2]
    cmp eax, 27h
    je dreapta
    cmp eax, 25h
    je stanga

    add cordYpiece, 15
    jmp iesire

    stanga:
    cmp cordXpiece, 170  ;de unde incepe patratul sa se miste
    jle iesire
    sub cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    dreapta:
    cmp cordXpiece, 415  ;pana unde are voie sa ajunga
    jge iesire
    add cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    iesire:
    mov eax, area_width
    imul eax, area_height
    imul eax, 4
    push eax
    push 255
    push area
    call memset
    add esp, 12

   piesa_T area, cordXpiece, cordYpiece
	
endm
     
;piesa t 1 jos

 miscare_T_1_jos macro
	
    local stanga, dreapta,iesire
    
    cmp cordYpiece, 575  ;pana unde are voie sa ajunga in jos
    jge iesire

    mov eax, [ebp+arg2]
    cmp eax, 27h
    je dreapta
    cmp eax, 25h
    je stanga

    add cordYpiece, 15
    jmp iesire

    stanga:
    cmp cordXpiece, 170  ;de unde incepe patratul sa se miste
    jle iesire
    sub cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    dreapta:
    cmp cordXpiece, 415  ;pana unde are voie sa ajunga
    jge iesire
    add cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    iesire:
    mov eax, area_width
    imul eax, area_height
    imul eax, 4
    push eax
    push 255
    push area
    call memset
    add esp, 12

   piesa_T_jos area, cordXpiece, cordYpiece
	
endm
      
;piesa t 1 stanga 

 miscare_T_1_stanga  macro
	
    local stanga, dreapta,iesire
    
    cmp cordYpiece, 560  ;pana unde are voie sa ajunga in jos
    jge iesire

    mov eax, [ebp+arg2]
    cmp eax, 27h
    je dreapta
    cmp eax, 25h
    je stanga

    add cordYpiece, 15
    jmp iesire

    stanga:
    cmp cordXpiece, 185  ;de unde incepe patratul sa se miste
    jle iesire
    sub cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    dreapta:
    cmp cordXpiece, 445  ;pana unde are voie sa ajunga
    jge iesire
    add cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    iesire:
    mov eax, area_width
    imul eax, area_height
    imul eax, 4
    push eax
    push 255
    push area
    call memset
    add esp, 12

   piesa_T_stanga area, cordXpiece, cordYpiece
	
endm
     
	  
	  
;piesa t 1 dreapta

miscare_T_1_dreapta  macro
	
    local stanga, dreapta,iesire
    
    cmp cordYpiece, 560  ;pana unde are voie sa ajunga in jos
    jge iesire

    mov eax, [ebp+arg2]
    cmp eax, 27h
    je dreapta
    cmp eax, 25h
    je stanga

    add cordYpiece, 15
    jmp iesire

    stanga:
    cmp cordXpiece, 170  ;de unde incepe patratul sa se miste
    jle iesire
    sub cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    dreapta:
    cmp cordXpiece, 430  ;pana unde are voie sa ajunga
    jge iesire
    add cordXpiece, 15
    add cordYpiece, 15
    jmp iesire

    iesire:
    mov eax, area_width
    imul eax, area_height
    imul eax, 4
    push eax
    push 255
    push area
    call memset
    add esp, 12

   piesa_T_dreapta area, cordXpiece, cordYpiece
	
endm
 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;zona pt rotire piese ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


      ;rotire_linie
     rotire_linie MACRO
	 local next1,altceva,rotire_0,final_rotire
    
	 mov edx, [ebp+arg2]
     cmp edx, 'D'
     je next1
     cmp edx, 'A'
     je next1
	 jmp altceva
    
     next1:
	 mov eax, 1
	 sub eax, rotire
	 mov rotire, eax
     jmp altceva
    
     altceva: 
	 cmp rotire, 0
	 je rotire_0
	 miscare_linie_orz
     jmp final_rotire
     rotire_0:
     miscare_linie
     final_rotire:
	 
	 endm



	;rotire_L_stanga
	 rotire_L_stanga MACRO
	 
	 local next,next1,rotire_0,rotire_1,rotire_2,final_rotire,altceva
	 mov edx, [ebp+arg2]
     cmp edx, 'D'
     je next
     cmp edx, 'A'
     je next1
	 jmp altceva
    
	 next:
	 mov ebx,2
	 sub ebx,rotire
	 mov rotire,ebx
	 jmp altceva
	 
     next1:
	 mov eax, 1
	 sub eax, rotire
	 mov rotire, eax
     jmp altceva
    
     altceva: 
	 cmp rotire, 0
	 je rotire_0
	 cmp rotire,1
	 je rotire_1
	 cmp rotire,2
	 je rotire_2
	 miscare_L_st_sus
     jmp final_rotire
     rotire_0:
     miscare_L_st
	 jmp final_rotire
	 rotire_1:
	 miscare_L_st_culcat_sus
	 jmp final_rotire
	 rotire_2:
	 miscare_L_st_culcat_jos
     final_rotire:
	 
	 endm
	 

	 
	 	;rotire_L_dreapta
	 rotire_L_dreapta MACRO
	 
	 local next,next1,rotire_0,rotire_1,rotire_2,final_rotire,altceva
	 mov edx, [ebp+arg2]
     cmp edx, 'D'
     je next
     cmp edx, 'A'
     je next1
	 jmp altceva
    
	 next:
	 mov ebx,2
	 sub ebx,rotire
	 mov rotire,ebx
	 jmp altceva
	 
     next1:
	 mov eax, 1
	 sub eax, rotire
	 mov rotire, eax
     jmp altceva
    
     altceva: 
	 cmp rotire, 0
	 je rotire_0
	 cmp rotire,1
	 je rotire_1
	 cmp rotire,2
	 je rotire_2
	 miscare_L_dr_sus
     jmp final_rotire
     rotire_0:
     miscare_L_dr
	 jmp final_rotire
	 rotire_1:
	 miscare_L_dr_culcat_sus
	 jmp final_rotire
	 rotire_2:
	 miscare_L_dr_culcat_jos
     final_rotire:
	 
	 endm
	 

	
	
		 ;rotire_Z_dreapta
	rotire_Z_dreapta MACRO
   local next1,altceva,rotire_0,final_rotire
    
	 mov edx, [ebp+arg2]
     cmp edx, 'D'
     je next1
     cmp edx, 'A'
     je next1
	 jmp altceva
    
     next1:
	 mov eax, 1
	 sub eax, rotire
	 mov rotire, eax
     jmp altceva
    
     altceva: 
	 cmp rotire, 0
	 je rotire_0
	 miscare_Z_dr
     jmp final_rotire
     rotire_0:
     miscare_Z_dr_sus
     final_rotire:
	 
	 endm

	 
	  ;rotire_Z_stanga
	 rotire_Z_stanga MACRO
   local next1,altceva,rotire_0,final_rotire
    
	 mov edx, [ebp+arg2]
     cmp edx, 'D'
     je next1
     cmp edx, 'A'
     je next1
	 jmp altceva
    
     next1:
	 mov eax, 1
	 sub eax, rotire
	 mov rotire, eax
     jmp altceva
    
     altceva: 
	 cmp rotire, 0
	 je rotire_0
	 miscare_Z_st
     jmp final_rotire
     rotire_0:
     miscare_Z_st_sus
     final_rotire:
	 
	 endm
	 
	
	 
	 
	  ;rotire_T
	    rotire_T  MACRO
	 
	 local next,next1,rotire_0,rotire_1,rotire_2,final_rotire,altceva
	 mov edx, [ebp+arg2]
     cmp edx, 'D'
     je next
     cmp edx, 'A'
     je next1
	 jmp altceva
    
	 next:
	 mov ebx,2
	 sub ebx,rotire
	 mov rotire,ebx
	 jmp altceva
	 
     next1:
	 mov eax, 1
	 sub eax, rotire
	 mov rotire, eax
     jmp altceva
    
     altceva: 
	 cmp rotire, 0
	 je rotire_0
	 cmp rotire,1
	 je rotire_1
	 cmp rotire,2
	 je rotire_2
	 miscare_T_1_dreapta
     jmp final_rotire
     rotire_0:
     miscare_T_1
	 jmp final_rotire
	 rotire_1:
	 miscare_T_1_stanga
	 jmp final_rotire
	 rotire_2:
	 miscare_T_1_jos
     final_rotire:
	 
	 endm 
	 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
generare_piesa_miscare MACRO x

	local nu_face_nimic,alta_piesa,alta_piesa_1,alta_piesa_2,alta_piesa_3,alta_piesa_4,alta_piesa_5
	cmp x,0
	jne alta_piesa
	miscare_patrat
	jmp nu_face_nimic
	alta_piesa:
    cmp x,1
	jne alta_piesa_1
	rotire_linie
	jmp nu_face_nimic
	alta_piesa_1:
	cmp x,2
	jne alta_piesa_2
	rotire_L_stanga
	jmp nu_face_nimic
	alta_piesa_2:
	cmp x,3
	jne alta_piesa_3
	rotire_L_dreapta
	jmp nu_face_nimic
	alta_piesa_3:
	cmp x,4
    jne alta_piesa_4
    rotire_Z_stanga
    jmp nu_face_nimic
    alta_piesa_4:	
	cmp x,5
	jne alta_piesa_5
	rotire_Z_dreapta
	jmp nu_face_nimic
	alta_piesa_5:
	rotire_T
	nu_face_nimic:
	
	 endm

	 
generare_piesa_statica MACRO x

  local nu_face_nimic,alta_piesa,alta_piesa_1,alta_piesa_2,alta_piesa_3,alta_piesa_4,alta_piesa_5

	cmp x,0
	jne alta_piesa
    piesa_patrat area, coordonata_x, coordonata_y
	jmp nu_face_nimic
	alta_piesa:
    cmp x,1
	jne alta_piesa_1
	piesa_linie_orizontala area, coordonata_x, coordonata_y
	jmp nu_face_nimic
	alta_piesa_1:
	cmp x,2
	jne alta_piesa_2
	piesa_L_stanga area, coordonata_x, coordonata_y
	jmp nu_face_nimic
	alta_piesa_2:
	cmp x,3
	jne alta_piesa_3
	piesa_L_dreapta  area, coordonata_x, coordonata_y
	jmp nu_face_nimic
	alta_piesa_3:
	cmp x,4
    jne alta_piesa_4
    piesa_Z_stanga  area, coordonata_x, coordonata_y
    jmp nu_face_nimic
    alta_piesa_4:	
	cmp x,5
	jne alta_piesa_5
	piesa_Z_dreapta  area, coordonata_x, coordonata_y
	jmp nu_face_nimic
	alta_piesa_5:
	piesa_T  area, coordonata_x, coordonata_y
	nu_face_nimic:
	
	 endm


umplere_matrice2 MACRO darea,x,y,tip
pusha
;la coordonatele x si y in matrice pune 0 1 sau 2  
mov eax,y
mov ebx,area_width2
mul ebx
add eax,x
shl eax,2
mov [area2+eax],tip
popa
endm
;-----------------------------------------------------------------------------------------------------------
linie MACRO darea,x,y,len,color
   local bucla_liniee,afara
	pusha
	mov eax,y
	mov ebx,area_width  ; 640 aflarea pozitiei click
    mul ebx
    add eax,x
    shl eax,2
    add eax, darea
    mov edx,len
bucla_liniee:
	mov dword ptr[eax], color ;linia
	add eax,4
	dec edx
	cmp edx,0
	jle afara
	jmp bucla_liniee
	afara:
popa
endm

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


draw proc
	push ebp
	mov ebp, esp
	pusha
	
	mov eax, [ebp+arg1]
	cmp eax, 1
	jz evt_click
	cmp eax, 2
	jz evt_timer
	; nu s-a efectuat click pe nimic
	
	cmp eax,3
	jz evt_click
	;mai jos e codul care intializeaza fereastra cu pixeli albi
	
	
	mov eax, area_width
	mov ebx, area_height
	mul ebx
	shl eax, 2
	push eax
	push 255
	push area
	call memset
	add esp, 12
	
	rdtsc
    xor edx,edx
    mov ebx,6
    div ebx
    mov switch_1,edx

	jmp afisare_litere
	
evt_click:
	
	
evt_timer:
	inc counter
	
afisare_litere:
   
   
    cmp valoare,32
	jne altceva
	mov valoare,0
	mov cordYpiece,125
	mov cordXpiece,290
	mov ebx,switch_1
	mov switch,ebx
    
	inc scor
	rdtsc
    xor edx,edx
    mov ebx,6
    div ebx
    mov switch_1,edx
    
	altceva:
	inc valoare

	generare_piesa_miscare switch
	generare_piesa_statica switch_1
	
	
	 
    make_text_macro 'T', area, 250, 10
	make_text_macro 'E', area, 260, 10
	make_text_macro 'T', area, 270, 10
	make_text_macro 'R', area, 280, 10
	make_text_macro 'I', area, 290, 10
	make_text_macro 'S', area, 300, 10
	
    make_text_macro 'S', area, 250, 40
	make_text_macro 'C', area, 260, 40
	make_text_macro 'O', area, 270, 40
	make_text_macro 'R', area, 280, 40
	
	
	make_text_macro 'V', area, 550, 630
	make_text_macro 'I', area, 560, 630
	make_text_macro 'N', area, 570, 630
	make_text_macro 'A', area, 580, 630
	make_text_macro 'T', area, 590, 630
	make_text_macro 'O', area, 600, 630
	make_text_macro 'R', area, 610, 630
	make_text_macro 'U', area, 620, 630
	
    make_text_macro 'D', area, 650, 630
	make_text_macro 'A', area, 660, 630
	make_text_macro 'I', area, 670, 630
	make_text_macro 'A', area, 680, 630
	make_text_macro 'N', area, 690, 630
	make_text_macro 'A', area, 700, 630

     
	;afisam valoarea counter-ului curent (sute, zeci si unitati)
	mov ebx, 10
	mov eax, scor
	;cifra unitatilor
	mov edx, 0
	div ebx
	add edx, '0'
	make_text_macro edx, area, 300, 40
	

       ;culoarea dreptunghiului mare
	linie_horizontal button_x, button_y, button_size1,300
	linie_horizontal button_x, button_size2+button_y, button_size1,300
	linie_vertical button_x, button_y, button_size2,300
	linie_vertical button_x + button_size1, button_y, button_size2,300
	
	
	


final_draw:
	popa
	mov esp, ebp
	pop ebp
	ret
draw endp



start:
    
	;alocam memorie pentru zona de desenat
	mov eax, area_width
	mov ebx, area_height
	mul ebx
	shl eax, 2
	push eax
	call malloc
	add esp, 4
	mov area, eax
	;apelam functia de desenare a ferestrei
	; typedef void (*DrawFunc)(int evt, int x, int y);
	; void __cdecl BeginDrawing(const char *title, int width, int height, unsigned int *area, DrawFunc draw);
	push offset draw
	push area
	push area_height
	push area_width
	push offset window_title
	call BeginDrawing
	add esp, 20
	
	;terminarea programului
	push 0
	call exit
end start


