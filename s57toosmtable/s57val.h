/* Copyright 2012 Malcolm Herring
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * For a copy of the GNU General Public License, see <http://www.gnu.org/licenses/>.
 */

/* Conversion of S-57 ATTL & ATVL values to OSeaM attribute tag values */

#ifndef S57VAL_H
#define S57VAL_H

#include "s57att.h"

typedef int Enum_t;
typedef enum {S, A, L, E, F, I} Conv_t; 
typedef struct VAL Val_t;
typedef struct LST Lst_t;

struct LST {
  Lst_t *next;
  Enum_t val;
};

struct VAL {
  Atta_t key;
  Conv_t type;
  union {
    char *s;
    char *a;
    Lst_t *l;
    Enum_t e;
    double f;
    long i;
  } val;
};

typedef  enum { MRK_UNKN, MRK_TOPB, MRK_BOTB, MRK_RTRI, MRK_LTRI, MRK_BTRI
} AddMRK_t;
typedef  enum { BNK_UNKN, BNK_LEFT, BNK_RGHT
} BnkWTW_t;
typedef enum {
  BCN_UNKN, BCN_STAK, BCN_WTHY, BCN_TOWR, BCN_LATT, BCN_PILE, BCN_CARN, BCN_BUOY, BCN_POLE, BCN_PRCH, BCN_POST
} BcnSHP_t;
typedef enum {
  BOY_UNKN, BOY_CONE, BOY_CAN, BOY_SPHR, BOY_PILR, BOY_SPAR, BOY_BARL, BOY_SUPR, BOY_ICE
} BoySHP;
typedef enum {
  BUI_UNKN, BUI_NONS, BUI_TOWR, BUI_SPIR, BUI_CPLA, BUI_HIRS, BUI_PYRD, BUI_CYLR, BUI_SPHR, BUI_CUBE
} BuiSHP_t;
typedef enum {
  FOG_UNKN, FOG_EXPL, FOG_DIA, FOG_SIRN, FOG_NAUT, FOG_REED, FOG_TYPH, FOG_BELL, FOG_WHIS, FOG_GONG, FOG_HORN
} CatFOG_t;
typedef enum {
  LIT_NONE, LIT_DIR, LIT_LEAD, LIT_AERO, LIT_AIR, LIT_FOG, LIT_FLDL, LIT_STRP, LIT_SUBS, LIT_SPOT, LIT_FRNT, LIT_REAR, LIT_LOWR,
  LIT_UPPR, LIT_MOIR, LIT_EMRG, LIT_BRNG, LIT_HORI, LIT_VERT
} CatLIT_t;
typedef enum {
  LMK_UNKN, LMK_CARN, LMK_CMTY, LMK_CHMY, LMK_DISH, LMK_FLAG, LMK_FLAR, LMK_MAST, LMK_WNDS, LMK_MNMT, LMK_CLMN, LMK_MEML, LMK_OBLK,
  LMK_STAT, LMK_CROS, LMK_DOME, LMK_RADR, LMK_TOWR, LMK_WNDM, LMK_WNDG, LMK_SPIR, LMK_BLDR, LMK_MNRT, LMK_WTRT
} CatLMK;
typedef enum { NMK_UNKN, NMK_NENT, NMK_CLSA, NMK_NOVK, NMK_NCOV, NMK_NPAS, NMK_NBRT, NMK_NBLL, NMK_NANK, NMK_NMOR, NMK_NTRN, NMK_NWSH, NMK_NPSL, NMK_NPSR, NMK_NMTC, NMK_NSPC, NMK_NWSK, NMK_NSLC,
  NMK_NUPC, NMK_NSLB, NMK_NWBK, NMK_NHSC, NMK_NLBG, NMK_MVTL, NMK_MVTR, NMK_MVTP, NMK_MVTS, NMK_KPTP, NMK_KPTS, NMK_CSTP, NMK_CSTS, NMK_STOP, NMK_SPDL, NMK_SHRN, NMK_KPLO, NMK_GWJN, NMK_GWCS,
  NMK_MKRC, NMK_LMDP, NMK_LMHR, NMK_LMWD, NMK_NAVR, NMK_CHDL, NMK_CHDR, NMK_CHTW, NMK_CHOW, NMK_OPTR, NMK_OPTL, NMK_PRTL, NMK_PRTR, NMK_ENTP, NMK_OVHC, NMK_WEIR, NMK_FERN, NMK_FERI, NMK_BRTP,
  NMK_BTLL, NMK_BTLS, NMK_BTRL, NMK_BTUP, NMK_BTP1, NMK_BTP2, NMK_BTP3, NMK_BTUN, NMK_BTN1, NMK_BTN2, NMK_BTN3, NMK_BTUM, NMK_BTU1, NMK_BTU2, NMK_BTU3, NMK_ANKP, NMK_MORP, NMK_VLBT, NMK_TRNA,
  NMK_SWWC, NMK_SWWR, NMK_SWWL, NMK_WRSA, NMK_WLSA, NMK_WRSL, NMK_WLSR, NMK_WRAL, NMK_WLAR, NMK_MWWC, NMK_MWWJ, NMK_MWAR, NMK_MWAL, NMK_WARL, NMK_WALR, NMK_PEND, NMK_DWTR, NMK_TELE, NMK_MTCP,
  NMK_SPCP, NMK_WSKP, NMK_SLCP, NMK_UPCP, NMK_SLBP, NMK_RADI, NMK_WTBP, NMK_HSCP, NMK_LBGP, NMK_KTPM, NMK_KTSM, NMK_KTMR, NMK_CRTP, NMK_CRTS, NMK_TRBM, NMK_RSPD,
} Cat_NMK_t;
typedef  enum { SCF_UNKN, SCF_VBTH, SCF_CLUB, SCF_BHST, SCF_SMKR, SCF_BTYD, SCF_INN, SCF_RSRT, SCF_CHDR, SCF_PROV, SCF_DCTR, SCF_PHRM, SCF_WTRT,
  SCF_FUEL, SCF_ELEC, SCF_BGAS, SCF_SHWR, SCF_LAUN, SCF_WC, SCF_POST, SCF_TELE, SCF_REFB, SCF_CARP, SCF_BTPK, SCF_CRVN, SCF_CAMP, SCF_PMPO,
  SCF_EMRT, SCF_SLPW, SCF_VMOR, SCF_SCRB, SCF_PCNC, SCF_MECH, SCF_SECS
} CatSCF;
typedef enum {
  RTB_UNKN, RTB_RAMK, RTB_RACN, RTB_LDG
} CatRTB_t;
typedef enum {
  SIT_UNKN, SIT_PRTC, SIT_PRTE, SIT_IPT, SIT_BRTH, SIT_DOCK, SIT_LOCK, SIT_FLDB, SIT_BRDG, SIT_DRDG, SIT_TCLT
} CatSIT_t;
typedef enum {
  SIW_UNKN, SIW_DNGR, SIW_OBST, SIW_CABL, SIW_MILY, SIW_DSTR, SIW_WTHR, SIW_STRM, SIW_ICE, SIW_TIME, SIW_TIDE, SIW_TSTR,
  SIW_TIDG, SIW_TIDS, SIW_DIVE, SIW_WTLG, SIW_VRCL, SIW_DPTH
} CatSIW_t;
typedef enum {
  CHR_UNKN, CHR_F, CHR_FL, CHR_LFL, CHR_Q, CHR_VQ, CHR_UQ, CHR_ISO, CHR_OC, CHR_IQ, CHR_IVQ, CHR_IUQ, CHR_MO, CHR_FFL, CHR_FLLFL,
  CHR_OCFL, CHR_FLFL, CHR_ALOC, CHR_ALLFL, CHR_ALFL, CHR_ALGR, CHR_QLFL, CHR_VQLFL, CHR_UQLFL, CHR_AL, CHR_ALFFL
} LitCHR_t;
typedef enum {
  COL_UNK, COL_WHT, COL_BLK, COL_RED, COL_GRN, COL_BLU, COL_YEL, COL_GRY, COL_BRN, COL_AMB, COL_VIO, COL_ORG, COL_MAG, COL_PNK
} ColCOL_t;
typedef enum {
  PAT_UNKN, PAT_HORI, PAT_VERT, PAT_DIAG, PAT_SQUR, PAT_STRP, PAT_BRDR, PAT_CROS, PAT_SALT
} ColPAT;
typedef enum {
  FNC_UNKN, FNC_HBRM, FNC_CSTM, FNC_HLTH, FNC_HOSP, FNC_POST, FNC_HOTL, FNC_RAIL, FNC_POLC, FNC_WPOL, FNC_PILO, FNC_PILL, FNC_BANK, FNC_DIST,
  FNC_TRNS, FNC_FCTY, FNC_POWR, FNC_ADMIN, FNC_EDUC, FNC_CHCH, FNC_CHPL, FNC_TMPL, FNC_PGDA, FNC_SHSH, FNC_BTMP, FNC_MOSQ, FNC_MRBT, FNC_LOOK,
  FNC_COMM, FNC_TV, FNC_RADO, FNC_RADR, FNC_LGHT, FNC_MCWV, FNC_COOL, FNC_OBS, FNC_TMBL, FNC_CLOK, FNC_CTRL, FNC_ASHM, FNC_STAD, FNC_BUSS
} FncFNC;
typedef enum {
  TOP_UNKN, TOP_CONE, TOP_ICONE, TOP_SPHR, TOP_ISD, TOP_CAN, TOP_BORD, TOP_SALT, TOP_CROS, TOP_CUBE, TOP_WEST, TOP_EAST, TOP_RHOM,
  TOP_NORTH, TOP_SOUTH, TOP_BESM, TOP_IBESM, TOP_FLAG, TOP_SPRH, TOP_SQUR, TOP_HRECT, TOP_VRECT, TOP_TRAP, TOP_ITRAP, TOP_TRI, TOP_ITRI,
  TOP_CIRC, TOP_CRSS, TOP_T, TOP_TRCL, TOP_CRCL, TOP_RHCL, TOP_CLTR, TOP_OTHR
} TopSHP;
typedef enum {
  SYS_UNKN, SYS_IALA, SYS_IALB, SYS_NONE, SYS_OTHR, SYS_CEVN, SYS_RIWR, SYS_BWR2, SYS_BNWR, SYS_PPWB
  } MarSYS;
typedef enum { BWW_UNKN, BWW_LEFT, BWW_RGHT
} BnkWTW;

extern void set_conv(char*);                      // Initialise character conversions for S57 file input

extern char *decodeValue(Attl_t, char*);          // Convert S57 attribute value to OSeaM attribute value string
//extern char *encodeValue(Attl_t, char*);        // Convert OSeaM attribute value string to S57 attribute value

extern char *stringValue(Val_t);                  // Convert OSeaM value struct to OSeaM attribute value string
extern Enum_t enumValue(char*, Atta_t);           // Convert OSeaM attribute value string to OSeaM enumeration
extern Val_t convertValue(char*, Atta_t);         // Convert OSeaM attribute value string to OSeaM value struct

typedef const struct {
  Enum_t atvl;
  char *val;
} s57val_t;

typedef const struct {
  Attl_t attl;
  Conv_t type;
  s57val_t *val;
} s57key_t;

s57key_t *findKey(Attl_t attl);

#endif
