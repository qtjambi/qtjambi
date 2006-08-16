
#include "type_ctors.h"
#include "type_environment.h"

namespace rpp {

uint qHash(const PrimitiveTypeCtor &ctor)
{
  return ::qHash(ctor.name());
}

uint qHash(const ArrayTypeCtor &ctor)
{
  return ::qHash(ctor.size());
}

uint qHash(const NamedTypeCtor &ctor)
{
  return ::qHash(ctor.name());
}

uint qHash(const MemberTypeCtor &ctor)
{
  return ::qHash(ctor.name());
}

uint qHash(const FunctionTypeCtor &ctor)
{
  return 0; // implement me
}

uint qHash(const TemplateTypeCtor &ctor)
{
  return 0; // implement me
}

TypeCtor::TypeCtor():
  _M_kind(Kind_InvalidTypeCtor)
{
}

TypeCtor::TypeCtor(int kind):
  _M_kind(kind)
{
}

TypeCtor::~TypeCtor()
{
}

void TypeVariable::appendTypeCtor(const TypeCtor *ctor)
{
  Q_ASSERT(isValid());
  _M_typeCtors.append(ctor);
}

void TypeVariable::prependTypeCtor(const TypeCtor *ctor)
{
  Q_ASSERT(isValid());
  _M_typeCtors.prepend(ctor);
}

TypeVariable &TypeVariable::addPointerTypeCtor()
{
  Q_ASSERT(isValid());
  _M_typeCtors.append(_M_typeEnvironment->pointerTypeCtor());
  return *this;
}

TypeVariable &TypeVariable::addReferenceTypeCtor()
{
  Q_ASSERT(isValid());
  _M_typeCtors.append(_M_typeEnvironment->referenceTypeCtor());
  return *this;
}

TypeVariable &TypeVariable::addConstTypeCtor()
{
  Q_ASSERT(isValid());
  _M_typeCtors.append(_M_typeEnvironment->constTypeCtor());
  return *this;
}

TypeVariable &TypeVariable::addVolatileTypeCtor()
{
  Q_ASSERT(isValid());
  _M_typeCtors.append(_M_typeEnvironment->volatileTypeCtor());
  return *this;
}

TypeVariable &TypeVariable::addPrimitiveTypeCtor(const QString &name)
{
  Q_ASSERT(isValid());
  _M_typeCtors.append(_M_typeEnvironment->primitiveTypeCtor(name));
  return *this;
}

TypeVariable &TypeVariable::addNamedTypeCtor(const QString &name)
{
  Q_ASSERT(isValid());
  _M_typeCtors.append(_M_typeEnvironment->namedTypeCtor(name));
  return *this;
}

TypeVariable &TypeVariable::addArrayTypeCtor(int size)
{
  Q_ASSERT(isValid());
  _M_typeCtors.append(_M_typeEnvironment->arrayTypeCtor(size));
  return *this;
}

TypeVariable &TypeVariable::addMemberTypeCtor(const QString &name)
{
  Q_ASSERT(isValid());
  _M_typeCtors.append(_M_typeEnvironment->memberTypeCtor(name));
  return *this;
}

TypeVariable &TypeVariable::addFunctionTypeCtor(const TypeVariable &returnType, const QList<TypeVariable> &arguments)
{
  Q_ASSERT(isValid());
  _M_typeCtors.append(_M_typeEnvironment->functionTypeCtor(returnType, arguments));
  return *this;
}

TypeVariable &TypeVariable::addTemplateTypeCtor(const QList<TypeVariable> &arguments)
{
  Q_ASSERT(isValid());
  _M_typeCtors.append(_M_typeEnvironment->templateTypeCtor(arguments));
  return *this;
}

bool TypeCtor::operator == (const TypeCtor &other) const
{
  return equalTo(other);
}

bool TypeCtor::operator != (const TypeCtor &other) const
{
  return ! equalTo(other);
}

int TypeCtor::kind() const
{
  return _M_kind;
}

bool TypeCtor::isInvalidTypeCtor() const
{
  return _M_kind == Kind_InvalidTypeCtor;
}

bool TypeCtor::isPrimitiveTypeCtor() const
{
  return _M_kind == Kind_PrimitiveTypeCtor;
}

bool TypeCtor::isPointerTypeCtor() const
{
  return _M_kind == Kind_PointerTypeCtor;
}

bool TypeCtor::isReferenceTypeCtor() const
{
  return _M_kind == Kind_ReferenceTypeCtor;
}

bool TypeCtor::isArrayTypeCtor() const
{
  return _M_kind == Kind_ArrayTypeCtor;
}

bool TypeCtor::isNamedTypeCtor() const
{
  return _M_kind == Kind_NamedTypeCtor;
}

bool TypeCtor::isConstTypeCtor() const
{
  return _M_kind == Kind_ConstTypeCtor;
}

bool TypeCtor::isVolatileTypeCtor() const
{
  return _M_kind == Kind_VolatileTypeCtor;
}

bool TypeCtor::isMemberTypeCtor() const
{
  return _M_kind == Kind_MemberTypeCtor;
}

bool TypeCtor::isFunctionTypeCtor() const
{
  return _M_kind == Kind_FunctionTypeCtor;
}

bool TypeCtor::isTemplateTypeCtor() const
{
  return _M_kind == Kind_TemplateTypeCtor;
}

const PrimitiveTypeCtor *TypeCtor::toPrimitiveTypeCtor() const
{
  return _M_kind == Kind_PrimitiveTypeCtor
    ? static_cast<const PrimitiveTypeCtor*> (this)
    : 0;
}

const PointerTypeCtor *TypeCtor::toPointerTypeCtor() const
{
  return _M_kind == Kind_PointerTypeCtor
    ? static_cast<const PointerTypeCtor*> (this)
    : 0;
}

const ReferenceTypeCtor *TypeCtor::toReferenceTypeCtor() const
{
  return _M_kind == Kind_ReferenceTypeCtor
    ? static_cast<const ReferenceTypeCtor*> (this)
    : 0;
}

const ArrayTypeCtor *TypeCtor::toArrayTypeCtor() const
{
  return _M_kind == Kind_ArrayTypeCtor
    ? static_cast<const ArrayTypeCtor*> (this)
    : 0;
}

const NamedTypeCtor *TypeCtor::toNamedTypeCtor() const
{
  return _M_kind == Kind_NamedTypeCtor
    ? static_cast<const NamedTypeCtor*> (this)
    : 0;
}

const ConstTypeCtor *TypeCtor::toConstTypeCtor() const
{
  return _M_kind == Kind_ConstTypeCtor
    ? static_cast<const ConstTypeCtor*> (this)
    : 0;
}

const VolatileTypeCtor *TypeCtor::toVolatileTypeCtor() const
{
  return _M_kind == Kind_VolatileTypeCtor
    ? static_cast<const VolatileTypeCtor*> (this)
    : 0;
}

const MemberTypeCtor *TypeCtor::toMemberTypeCtor() const
{
  return _M_kind == Kind_MemberTypeCtor
    ? static_cast<const MemberTypeCtor*> (this)
    : 0;
}

const FunctionTypeCtor *TypeCtor::toFunctionTypeCtor() const
{
  return _M_kind == Kind_FunctionTypeCtor
    ? static_cast<const FunctionTypeCtor*> (this)
    : 0;
}

const TemplateTypeCtor *TypeCtor::toTemplateTypeCtor() const
{
  return _M_kind == Kind_TemplateTypeCtor
    ? static_cast<const TemplateTypeCtor*> (this)
    : 0;
}

PrimitiveTypeCtor::PrimitiveTypeCtor():
  TypeCtor(Kind_PrimitiveTypeCtor)
{
}

PrimitiveTypeCtor::~PrimitiveTypeCtor()
{
}

QString PrimitiveTypeCtor::name() const
{
  return _M_name;
}

void PrimitiveTypeCtor::setName(const QString &name)
{
  _M_name = name;
}

PointerTypeCtor::PointerTypeCtor():
  TypeCtor(Kind_PointerTypeCtor)
{
}

PointerTypeCtor::~PointerTypeCtor()
{
}


ReferenceTypeCtor::ReferenceTypeCtor():
  TypeCtor(Kind_ReferenceTypeCtor)
{
}

ReferenceTypeCtor::~ReferenceTypeCtor()
{
}

ArrayTypeCtor::ArrayTypeCtor():
  TypeCtor(Kind_ArrayTypeCtor),
  _M_size(0)
{
}

ArrayTypeCtor::~ArrayTypeCtor()
{
}

int ArrayTypeCtor::size() const
{
  return _M_size;
}

void ArrayTypeCtor::setSize(int size)
{
  _M_size = size;
}

NamedTypeCtor::NamedTypeCtor():
  TypeCtor(Kind_NamedTypeCtor)
{
}

NamedTypeCtor::~NamedTypeCtor()
{
}

QString NamedTypeCtor::name() const
{
  return _M_name;
}

void NamedTypeCtor::setName(const QString &name)
{
  _M_name = name;
}

ConstTypeCtor::ConstTypeCtor():
  TypeCtor(Kind_ConstTypeCtor)
{
}

ConstTypeCtor::~ConstTypeCtor()
{
}

VolatileTypeCtor::VolatileTypeCtor():
  TypeCtor(Kind_VolatileTypeCtor)
{
}

VolatileTypeCtor::~VolatileTypeCtor()
{
}

MemberTypeCtor::MemberTypeCtor():
  TypeCtor(Kind_MemberTypeCtor)
{
}

MemberTypeCtor::~MemberTypeCtor()
{
}

QString MemberTypeCtor::name() const
{
  return _M_name;
}

void MemberTypeCtor::setName(const QString &name)
{
  _M_name = name;
}

FunctionTypeCtor::FunctionTypeCtor():
  TypeCtor(Kind_FunctionTypeCtor)
{
}

FunctionTypeCtor::~FunctionTypeCtor()
{
}

TypeVariable FunctionTypeCtor::returnType() const
{
  return _M_returnType;
}

void FunctionTypeCtor::setReturnType(const TypeVariable &returnType)
{
  _M_returnType = returnType;
}

QList<TypeVariable> FunctionTypeCtor::arguments() const
{
  return _M_arguments;
}

void FunctionTypeCtor::clearArguments()
{
  _M_arguments.clear();
}

void FunctionTypeCtor::setArguments(const QList<TypeVariable> &arguments)
{
  _M_arguments = arguments;
}

void FunctionTypeCtor::addArgument(const TypeVariable &argument)
{
  _M_arguments.append(argument);
}

TemplateTypeCtor::TemplateTypeCtor():
  TypeCtor(Kind_TemplateTypeCtor)
{
}

TemplateTypeCtor::~TemplateTypeCtor()
{
}

QList<TypeVariable> TemplateTypeCtor::arguments() const
{
  return _M_arguments;
}

void TemplateTypeCtor::clearArguments()
{
  _M_arguments.clear();
}

void TemplateTypeCtor::setArguments(const QList<TypeVariable> &arguments)
{
  _M_arguments = arguments;
}

void TemplateTypeCtor::addArgument(const TypeVariable &argument)
{
  _M_arguments.append(argument);
}

bool PrimitiveTypeCtor::equalTo(const TypeCtor &other) const
{
  if (const PrimitiveTypeCtor *ctor = other.toPrimitiveTypeCtor())
    return _M_name == ctor->_M_name;

  return false;
}

bool PointerTypeCtor::equalTo(const TypeCtor &other) const
{
  return other.isPointerTypeCtor();
}

bool ReferenceTypeCtor::equalTo(const TypeCtor &other) const
{
  return other.isReferenceTypeCtor();
}

bool ArrayTypeCtor::equalTo(const TypeCtor &other) const
{
  if (const ArrayTypeCtor *ctor = other.toArrayTypeCtor())
    return _M_size == ctor->_M_size;

  return false;
}

bool NamedTypeCtor::equalTo(const TypeCtor &other) const
{
  if (const NamedTypeCtor *ctor = other.toNamedTypeCtor())
    return _M_name == ctor->_M_name;

  return false;
}

bool ConstTypeCtor::equalTo(const TypeCtor &other) const
{
  return other.isConstTypeCtor();
}

bool VolatileTypeCtor::equalTo(const TypeCtor &other) const
{
  return other.isVolatileTypeCtor();
}

bool MemberTypeCtor::equalTo(const TypeCtor &other) const
{
  if (const MemberTypeCtor *ctor = other.toMemberTypeCtor())
    return _M_name == ctor->_M_name;

  return false;
}

bool FunctionTypeCtor::equalTo(const TypeCtor &other) const
{
  if (const FunctionTypeCtor *ctor = other.toFunctionTypeCtor())
    return _M_returnType == ctor->_M_returnType
        && _M_arguments == ctor->_M_arguments;

  return false;
}

bool TemplateTypeCtor::equalTo(const TypeCtor &other) const
{
  if (const TemplateTypeCtor *ctor = other.toTemplateTypeCtor())
    return _M_arguments == ctor->_M_arguments;

  return false;
}

} // namespace rpp

// kate: indent-width 2


