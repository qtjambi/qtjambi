
#ifndef STACKELEMENT_H_
#define STACKELEMENT_H_

class CustomFunction;
class TemplateEntry;
class TemplateInstance;
class TypeEntry;

class StackElement {
    public:
        enum ElementType {
            None = 0x0,

            // Type tags (0x1, ... , 0xff)
            ObjectTypeEntry      = 0x1,
            ValueTypeEntry       = 0x2,
            InterfaceTypeEntry   = 0x3,
            NamespaceTypeEntry   = 0x4,
            ComplexTypeEntryMask = 0xf,

            // Non-complex type tags (0x10, 0x20, ... , 0xf0)
            PrimitiveTypeEntry   = 0x10,
            EnumTypeEntry        = 0x20,
            TypeEntryMask        = 0xff,

            // Simple tags (0x100, 0x200, ... , 0xf00)
            ExtraIncludes               = 0x100,
            Include                     = 0x200,
            ModifyFunction              = 0x300,
            ModifyField                 = 0x400,
            Root                        = 0x500,
            CustomMetaConstructor       = 0x600,
            CustomMetaDestructor        = 0x700,
            ArgumentMap                 = 0x800,
            SuppressedWarning           = 0x900,
            Rejection                   = 0xa00,
            LoadTypesystem              = 0xb00,
            RejectEnumValue             = 0xc00,
            Template                    = 0xd00,
            TemplateInstanceEnum        = 0xe00,
            Replace                     = 0xf00,
            SimpleMask                  = 0xf00,

            // Code snip tags (0x1000, 0x2000, ... , 0xf000)
            InjectCode =           0x1000,
            InjectCodeInFunction = 0x2000,
            CodeSnipMask =         0xf000,

            // Function modifier tags (0x010000, 0x020000, ... , 0xf00000)
            Access                   = 0x010000,
            Removal                  = 0x020000,
            Rename                   = 0x040000,
            ModifyArgument           = 0x080000,
            FunctionModifiers        = 0xff0000,

            // Argument modifier tags (0x01000000 ... 0xf0000000)
            ConversionRule           = 0x01000000,
            ReplaceType              = 0x02000000,
            ReplaceDefaultExpression = 0x04000000,
            RemoveArgument           = 0x08000000,
            DefineOwnership          = 0x10000000,
            RemoveDefaultExpression  = 0x20000000,
            NoNullPointers           = 0x40000000,
            ReferenceCount           = 0x80000000,
            ArgumentModifiers        = 0xff000000
        };

        StackElement(StackElement *p) : entry(0), type(None), parent(p) { }

        TypeEntry *entry;
        ElementType type;
        StackElement *parent;

        union {
            TemplateInstance *templateInstance;
            TemplateEntry *templateEntry;
            CustomFunction *customFunction;
        } value;
};

#endif
