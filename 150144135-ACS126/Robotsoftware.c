/*
 File: Robotsoftware.c
 Author: Tingyue Jia and Ebuka Isiadinso
 Created: 13/02/2016
 Purpose: ACS126 Robot Project
*/

#include <xc.h>
#include <p18f2221.h>
#include <stdio.h>
#include <stdlib.h>

#pragma config OSC = HS                     //High speed resonator
#pragma config WDT = OFF                    //Watchdog timer off
#pragma config LVP = OFF                    //Low voltage programing disabled
#pragma config PWRT = ON                    //Power up timer on

#define _XTAL_FREQ 10000000                 //Defines clock frequency for __delay_10ms()

#define LED_1 PORTBbits.RB2                 //LED 1
#define LED_2 PORTBbits.RB3                 //LED 2
#define LED_3 PORTBbits.RB4                 //LED 3
#define LED_4 PORTBbits.RB5                 //LED 4

#define motor_L_B PORTAbits.RA4             //Backward - Left motor
#define motor_L_F PORTAbits.RA5             //Forward - Left motor
#define motor_R_B PORTBbits.RB0             //Backward - Right motor
#define motor_R_F PORTBbits.RB1             //Forward - Right motor

#define IRB_DL PORTCbits.RC3                //Left - IR Beacon detector
#define IRB_DR PORTCbits.RC4                //Right - IR Beacon detector

#define LeftWheelEncoder PORTCbits.RC0      //Left Wheel encoder
#define RightWheelEncoder PORTCbits.RC5     //Right Wheel encoder

void wait10ms(int del);                     //Creates a delay in multiples of 10ms
void wait1ms(int del);                      //Creates a delay in multiples of 5ms
void setupADC(void);                        //Configures A/D

void wheel(void);                           //Configures wheel encoders
void position(void);                        //Configures position

unsigned int readADCL(void);                //Reads ADCL
unsigned int readADCR(void);                //Reads ADCR

//Configures motor movements
void moveforward(void);
void moveright(void);
void moveleft(void);

volatile unsigned char LeftWheelState = 0;  //Initializes left wheel state
volatile unsigned char RightWheelState = 0; //Initializes right wheel state


int setpoint_distance = 400,                //Distance set point and motor speed
markspace = 200;

int main(void)
{
   ADCON1 = 0b00001110;                     //Sets voltage reference and port A0 as analogue input
    TRISA = 0b11001111;                     //Input or output for Port A
    TRISB = 0;                              //Output for Port B
    TRISC = 0b00111001;                     //Output for Port C

    setupADC();                             //Configures A/D

    PR2 = 0b11111111;                       //Sets period of Pulse width modulator (PWM)
    T2CON = 0b00000111;                     //Timer 2 (TMR2) on with Prescaler = 16

    CCP1CON = 0b00001100;                   //0x0c enables PWM module CCP1
    CCP2CON = 0b00001100;                   //0x0c enables PWM module CCP2

    CCPR1L = markspace;                     //Loads duty cycle into CCP1CON and PWM starts
    CCPR1L = markspace;                     //Loads duty cycle into CCP2CON and PWM starts

    //Sets all motor movement to 0
    motor_L_B = 0;
    motor_L_F = 0;
    motor_R_B = 0;
    motor_R_F = 0;

    //Waits for 3 seconds
    wait10ms(150);
    wait10ms(150);

    PORTB = 0b00111100;                     //All LEDs go on
    wait10ms(50);                           //Waits for 0.5 seconds
    PORTB = 0b00000000;                     //All LEDs go off
    wait10ms(50);                           //Waits for 0.5 seconds

    while(1)
    {
        moveforward();                      //Motor moves forward

        if(IRB_DL==0 && IRB_DR==0)          //Both sensors detect beacon
        {
            LED_1 = 1;                      //LED 1 goes on
            LED_4 = 1;                      //LED 4 goes on
            moveforward();                  //Motor moves forward
            wait1ms(1);                     //Waits for 1 ms
        }

        else                                //Both sensor do not detect beacon
        {
            LED_1 = 0;                      //LED 1 goes off
            LED_4 = 0;                      //LED 4 goes off
        }

         if(IRB_DL==1 && IRB_DR==0)         //Only right sensor detects beacon
        {
            LED_1 = 1;                      //LED 1 goes on
            moveright();                    //Motor moves right
            wait1ms(1);                     //Waits for 1 ms
            motor_R_B = 0;                  //Backward - Right motor off
        }

        else                                //Right sensor does not detect beacon
        {
            LED_1 = 0;                      //LED 1 goes off
        }

         if(IRB_DL==0 && IRB_DR==1)         //Only left sensor detects beacon
        {
            LED_4 = 1;                      //LED 4 goes on
            moveleft();                     //Motor moves left
            wait1ms(1);                     //Waits for 1 ms
            motor_L_B = 0;                  //Backward - Left motor off
        }

        else                                //Left sensor does not detect beacon
        {
            LED_4 = 0;                      //LED 4 goes off
        }

        if(IRB_DL==1 && IRB_DR==1)
        {
            moveleft();
            wait1ms(1);
            motor_L_B = 0;
        }

        else
        {
            LED_4 = 0;
            LED_1 = 0;
        }
        
        //When right sensor detects an object
        if(readADCL() <= setpoint_distance && readADCR() >= setpoint_distance)
        {
            LED_3 = 1;                      //LED 3 goes on
            moveleft();                     //Motor moves left
            wait10ms(50);                   //Waits for 0.05 seconds
            motor_L_B = 0;                  //Backward - Left motor off
            motor_L_F = 1;                  //Forward - Left motor on
            wait10ms(50);                   //Waits for 0.05 seconds
        }

        //when left sensor detects an obejct
        else if(readADCL() >= setpoint_distance && readADCR() <= setpoint_distance)
        {
            LED_2 = 1;                      //LED 2 goes on
            moveright();                    //Motor moves right
            wait10ms(50);                   //Waits for 0.05 seconds
            motor_R_B = 0;                  //Backward - Right motor off
            motor_R_F = 1;                  //Forward - Right motor on
            wait10ms(50);                   //Waits for 0.05 seconds
        }
    }
}

void wait10ms(int del)                      //Delay function
{
    unsigned char c;
    for(c=0;c<del;c++)
        __delay_ms(10);
    return;
}

void wait1ms(int del)                       //Delay function
{
    unsigned char c;
    for(c=0;c<del;c++)
        __delay_ms(1);
    return;
}

void setupADC(void)                         //Configures A/D
{
    ADCON2bits.ADCS0=0;                     //Fosc/32
    ADCON2bits.ADCS1=1;
    ADCON2bits.ADCS2=0;
    ADCON2bits.ADFM=1;                      //A/D result right justified
    ADCON1=0b00001110;                      //Sets voltage reference and port A0 as A/D
    ADCON0bits.ADON=1;                      //Turn on ADC
}

unsigned int readADCL(void)                 //Reads port ANO
{
    ADCON0bits.CHS0=0;                      //0000, channel 0 is set
    ADCON0bits.CHS1=0;                      //Uses binary number to choose ADC channel
    ADCON0bits.CHS2=0;                      //e.g. 1001 channel 9 set
    ADCON0bits.CHS3=0;                      //Channel 0 set
    ADCON0bits.GO=1;
    while(ADCON0bits.GO);                   //Does nothing while conversion is in progress
    return ((ADRESH<<8)+ADRESL);            //Combines high and low bytes into one 16 bit value and returns
                                            //result (A/D value 0-1023)
}

unsigned int readADCR(void)                 //Reads port AN1
{
    ADCON0bits.CHS0=1;                      //1000, channel 1 is set
    ADCON0bits.CHS1=0;                      //Uses binary number to choose ADC channel
    ADCON0bits.CHS2=0;                      //e.g. 1001 channel 9 set
    ADCON0bits.CHS3=0;                      //Channel 0 set
    ADCON0bits.GO=1;
    while(ADCON0bits.GO);                   //Does nothing while conversion is in progress
    return ((ADRESH<<8)+ADRESL);            //Combines high and low bytes into one 16 bit value and returns
                                            //result (A/D value 0-1023)
}

void wheel(void)                            //Wheel encoder function
{
    while (1)

    if (LeftWheelEncoder == 0)              //Sets the initial state of the left wheel encoder
    {
        LED_2 = 0;                          //States motor based upon the current encoder position
        LeftWheelState = 0;
      
    }
    else if (LeftWheelEncoder == 1)
    {
        LED_2 = 1;
        LeftWheelState = 1;
        
    }
    
    if (RightWheelEncoder == 0)             //Sets the initial state of the right wheel encoder
    {
        LED_3 = 0;                          //States motor based upon the current encoder position
        RightWheelState = 0;
       
    }
    else if (RightWheelEncoder == 1)
    {
        LED_3 = 1;
        RightWheelState = 1;
      
    }

    unsigned char i;
    for (i=0;i<=16;i++)
        return;
}

//Motor movements
void moveforward(void)                      //Motor to move forward
{
    motor_L_B=0;                            //Backward - Left motor off
    motor_L_F=1;                            //Forward - Left motor on
    motor_R_B=0;                            //Backward - Right motor off
    motor_R_F=1;                            //Forward - Right motor on
}

void moveleft(void)                         //Motor to move left
{
    motor_L_B=1;                            //Backward - Left motor on
    motor_L_F=0;                            //Forward - Left motor off
    motor_R_B=0;                            //Backward - Right motor off
    motor_R_F=1;                            //Forward - Right motor on
}

void moveright(void)                        //Motor to move right
{
    motor_L_B=0;                            //Backward - Left motor off
    motor_L_F=1;                            //Forward - Left motor on
    motor_R_B=1;                            //Backward - Right motor on
    motor_R_F=0;                            //Forward - Right motor off
}
