
#include "type_environment.h"

namespace rpp {

TypeEnvironment::TypeEnvironment()
{
}

TypeEnvironment::~TypeEnvironment()
{
}

TypeVariable TypeEnvironment::newTypeVariable()
{
  TypeVariable tv(this);
  return tv;
}

const PointerTypeCtor *TypeEnvironment::pointerTypeCtor()
{
  return &_M_pointerTypeCtor;
}

const ReferenceTypeCtor *TypeEnvironment::referenceTypeCtor()
{
  return &_M_referenceTypeCtor;
}

const ConstTypeCtor *TypeEnvironment::constTypeCtor()
{
  return &_M_constTypeCtor;
}

const VolatileTypeCtor *TypeEnvironment::volatileTypeCtor()
{
  return &_M_volatileTypeCtor;
}

const PrimitiveTypeCtor *TypeEnvironment::primitiveTypeCtor(const QString &name)
{
  PrimitiveTypeCtor tmp;
  tmp.setName(name);

  return &*_M_primitiveTypeCtors.insert(tmp);
}

const NamedTypeCtor *TypeEnvironment::namedTypeCtor(const QString &name)
{
  NamedTypeCtor tmp;
  tmp.setName(name);

  return &*_M_namedTypeCtors.insert(tmp);
}

const ArrayTypeCtor *TypeEnvironment::arrayTypeCtor(int size)
{
  ArrayTypeCtor tmp;
  tmp.setSize(size);

  return &*_M_arrayTypeCtors.insert(tmp);
}

const MemberTypeCtor *TypeEnvironment::memberTypeCtor(const QString &name)
{
  MemberTypeCtor tmp;
  tmp.setName(name);

  return &*_M_memberTypeCtors.insert(tmp);
}

const FunctionTypeCtor *TypeEnvironment::functionTypeCtor(const TypeVariable &returnType, const QList<TypeVariable> &arguments)
{
  FunctionTypeCtor tmp;
  tmp.setReturnType(returnType);
  tmp.setArguments(arguments);

  return &*_M_functionTypeCtors.insert(tmp);
}

const TemplateTypeCtor *TypeEnvironment::templateTypeCtor(const QList<TypeVariable> &arguments)
{
  TemplateTypeCtor tmp;
  tmp.setArguments(arguments);

  return &*_M_templateTypeCtors.insert(tmp);
}


} // namespace rpp

// kate: indent-width 2
