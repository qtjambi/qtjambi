/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $CPP_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

struct QLatin1Char
{
public:
    inline explicit QLatin1Char(char c) : ch(c) {}
    inline const char toLatin1() const { return ch; }
    inline const ushort unicode() const { return ushort(uchar(ch)); }

private:
    const char ch;
};

class __declspec(dllimport) QChar {
public:
    QChar();

    QChar(char c);
    QChar(uchar c);

    QChar(QLatin1Char);
    QChar(uchar c, uchar r);
    inline QChar(ushort rc) : ucs(rc){}
    QChar(short rc);
    QChar(uint rc);
    QChar(int rc);
    enum SpecialCharacter {
        Null = 0x0000,
        Nbsp = 0x00a0,
        ReplacementCharacter = 0xfffd,
        ObjectReplacementCharacter = 0xfffc,
        ByteOrderMark = 0xfeff,
        ByteOrderSwapped = 0xfffe,







        ParagraphSeparator = 0x2029,
        LineSeparator = 0x2028
    };
    QChar(SpecialCharacter sc);



    enum Category
    {
        NoCategory,

        Mark_NonSpacing,
        Mark_SpacingCombining,
        Mark_Enclosing,

        Number_DecimalDigit,
        Number_Letter,
        Number_Other,

        Separator_Space,
        Separator_Line,
        Separator_Paragraph,

        Other_Control,
        Other_Format,
        Other_Surrogate,
        Other_PrivateUse,
        Other_NotAssigned,

        Letter_Uppercase,
        Letter_Lowercase,
        Letter_Titlecase,
        Letter_Modifier,
        Letter_Other,

        Punctuation_Connector,
        Punctuation_Dash,
        Punctuation_Open,
        Punctuation_Close,
        Punctuation_InitialQuote,
        Punctuation_FinalQuote,
        Punctuation_Other,

        Symbol_Math,
        Symbol_Currency,
        Symbol_Modifier,
        Symbol_Other,

        Punctuation_Dask = Punctuation_Dash
    };

    enum Direction
    {
        DirL, DirR, DirEN, DirES, DirET, DirAN, DirCS, DirB, DirS, DirWS, DirON,
        DirLRE, DirLRO, DirAL, DirRLE, DirRLO, DirPDF, DirNSM, DirBN
    };

    enum Decomposition
    {
        NoDecomposition,
        Canonical,
        Font,
        NoBreak,
        Initial,
        Medial,
        Final,
        Isolated,
        Circle,
        Super,
        Sub,
        Vertical,
        Wide,
        Narrow,
        Small,
        Square,
        Compat,
        Fraction




    };

    enum Joining
    {
        OtherJoining, Dual, Right, Center
    };

    enum CombiningClass
    {
        Combining_BelowLeftAttached       = 200,
        Combining_BelowAttached           = 202,
        Combining_BelowRightAttached      = 204,
        Combining_LeftAttached            = 208,
        Combining_RightAttached           = 210,
        Combining_AboveLeftAttached       = 212,
        Combining_AboveAttached           = 214,
        Combining_AboveRightAttached      = 216,

        Combining_BelowLeft               = 218,
        Combining_Below                   = 220,
        Combining_BelowRight              = 222,
        Combining_Left                    = 224,
        Combining_Right                   = 226,
        Combining_AboveLeft               = 228,
        Combining_Above                   = 230,
        Combining_AboveRight              = 232,

        Combining_DoubleBelow             = 233,
        Combining_DoubleAbove             = 234,
        Combining_IotaSubscript           = 240
    };

    enum UnicodeVersion {
        Unicode_Unassigned,
        Unicode_1_1,
        Unicode_2_0,
        Unicode_2_1_2,
        Unicode_3_0,
        Unicode_3_1,
        Unicode_3_2,
        Unicode_4_0
    };


    int digitValue() const;
    QChar toLower() const;
    QChar toUpper() const;

    Category category() const;
    Direction direction() const;
    Joining joining() const;
    bool hasMirrored() const;
    inline bool isLower() const { return category() == Letter_Lowercase; }
    inline bool isUpper() const { return category() == Letter_Uppercase; }




    QChar mirroredChar() const;
    QString decomposition() const;
    Decomposition decompositionTag() const;
    unsigned char combiningClass() const;

    UnicodeVersion unicodeVersion() const;

    const char toAscii() const;
    inline const char toLatin1() const;
    inline const ushort unicode() const { return ucs; }



    inline ushort &unicode() { return ucs; }


    static QChar fromAscii(char c);
    static QChar fromLatin1(char c);

    inline bool isNull() const { return ucs == 0; }
    bool isPrint() const;
    bool isPunct() const;
    bool isSpace() const;
    bool isMark() const;
    bool isLetter() const;
    bool isNumber() const;
    bool isLetterOrNumber() const;
    bool isDigit() const;
    bool isSymbol() const;

    inline uchar cell() const { return uchar(ucs & 0xff); }
    inline uchar row() const { return uchar((ucs>>8)&0xff); }
    inline void setCell(uchar cell);
    inline void setRow(uchar row);











private:




    ushort ucs;
} ;
