
#ifndef RPP_TYPE_CTORS_H
#define RPP_TYPE_CTORS_H

#include <QtCore/QList>
#include <QtCore/QString>
#include <QtCore/QHash>

namespace rpp {

class TypeEnvironment;

class TypeCtor;
class InvalidTypeCtor;
class PrimitiveTypeCtor;
class PointerTypeCtor;
class ReferenceTypeCtor;
class ArrayTypeCtor;
class NamedTypeCtor;
class ConstTypeCtor;
class VolatileTypeCtor;
class MemberTypeCtor;
class FunctionTypeCtor;
class TemplateTypeCtor;

enum TypeCtorKind {
  Kind_InvalidTypeCtor,
  Kind_PrimitiveTypeCtor,
  Kind_PointerTypeCtor,
  Kind_ReferenceTypeCtor,
  Kind_ArrayTypeCtor,
  Kind_NamedTypeCtor,
  Kind_ConstTypeCtor,
  Kind_VolatileTypeCtor,
  Kind_MemberTypeCtor,
  Kind_FunctionTypeCtor,
  Kind_TemplateTypeCtor,
};

class TypeVariable
{
public:
  typedef QList<const TypeCtor *> TypeCtorSequence;

public:
  inline TypeVariable():
    _M_typeEnvironment(0)
  {
  }

  inline TypeVariable(TypeEnvironment *typeEnvironment):
    _M_typeEnvironment(typeEnvironment)
  {
  }

  inline TypeVariable(const TypeVariable &other):
    _M_typeEnvironment(other._M_typeEnvironment),
    _M_typeCtors(other._M_typeCtors)
  {
  }

  inline TypeVariable &operator = (const TypeVariable &other)
  {
    _M_typeEnvironment = other._M_typeEnvironment;
    _M_typeCtors = other._M_typeCtors;
    return *this;
  }

  inline bool isValid() const { return typeEnvironment() != 0; }
  inline bool isEmpty() const { Q_ASSERT(isValid()); return _M_typeCtors.isEmpty(); }
  inline void clear() { Q_ASSERT(isValid()); _M_typeCtors.clear(); }

  inline bool operator == (const TypeVariable &other) const
  {
    Q_ASSERT(isValid());

    return _M_typeEnvironment == other._M_typeEnvironment
        && _M_typeCtors == other._M_typeCtors;
  }

  inline bool operator != (const TypeVariable &other) const
  {
    Q_ASSERT(isValid());

    return _M_typeEnvironment != other._M_typeEnvironment
        || _M_typeCtors != other._M_typeCtors;
  }

  inline TypeCtorSequence typeCtors() const { return _M_typeCtors; }
  inline TypeCtorSequence::ConstIterator firstTypeCtor() const { return _M_typeCtors.begin(); }
  inline TypeCtorSequence::ConstIterator lastTypeCtor() const { return _M_typeCtors.end(); }
  inline TypeCtorSequence::Iterator firstTypeCtor() { return _M_typeCtors.begin(); }
  inline TypeCtorSequence::Iterator lastTypeCtor() { return _M_typeCtors.end(); }

  inline void removeFirst() { _M_typeCtors.removeFirst(); }
  inline void removeLast() { _M_typeCtors.removeLast(); }

  inline TypeEnvironment *typeEnvironment() const { return _M_typeEnvironment; }

  void addTypeCtor(const TypeCtor *ctor);

  TypeVariable &addPointerTypeCtor();
  TypeVariable &addReferenceTypeCtor();
  TypeVariable &addConstTypeCtor();
  TypeVariable &addVolatileTypeCtor();
  TypeVariable &addPrimitiveTypeCtor(const QString &name);
  TypeVariable &addNamedTypeCtor(const QString &name);
  TypeVariable &addArrayTypeCtor(int size);
  TypeVariable &addMemberTypeCtor(const QString &name);
  TypeVariable &addFunctionTypeCtor(const TypeVariable &returnType, const QList<TypeVariable> &arguments);
  TypeVariable &addTemplateTypeCtor(const QList<TypeVariable> &arguments);

  void appendTypeCtor(const TypeCtor *ctor);
  void prependTypeCtor(const TypeCtor *ctor);

private:
  TypeEnvironment *_M_typeEnvironment;
  TypeCtorSequence _M_typeCtors;
};

class TypeCtor
{
public:
  TypeCtor();
  TypeCtor(int kind);

  virtual ~TypeCtor();

  virtual bool equalTo(const TypeCtor &other) const = 0;

  int kind() const;
  bool isInvalidTypeCtor() const;
  bool isPrimitiveTypeCtor() const;
  bool isPointerTypeCtor() const;
  bool isReferenceTypeCtor() const;
  bool isArrayTypeCtor() const;
  bool isNamedTypeCtor() const;
  bool isConstTypeCtor() const;
  bool isVolatileTypeCtor() const;
  bool isMemberTypeCtor() const;
  bool isFunctionTypeCtor() const;
  bool isTemplateTypeCtor() const;

  const PrimitiveTypeCtor *toPrimitiveTypeCtor() const;
  const PointerTypeCtor *toPointerTypeCtor() const;
  const ReferenceTypeCtor *toReferenceTypeCtor() const;
  const ArrayTypeCtor *toArrayTypeCtor() const;
  const NamedTypeCtor *toNamedTypeCtor() const;
  const ConstTypeCtor *toConstTypeCtor() const;
  const VolatileTypeCtor *toVolatileTypeCtor() const;
  const MemberTypeCtor *toMemberTypeCtor() const;
  const FunctionTypeCtor *toFunctionTypeCtor() const;
  const TemplateTypeCtor *toTemplateTypeCtor() const;

  bool operator == (const TypeCtor &other) const; // ### remove me!! QHash/QSet I hate you :-)
  bool operator != (const TypeCtor &other) const; // ### remove me!! QHash/QSet I hate you :-)

private:
  int _M_kind;
};

class PrimitiveTypeCtor: public TypeCtor
{
public:
  PrimitiveTypeCtor();
  virtual ~PrimitiveTypeCtor();

  QString name() const;
  void setName(const QString &name);

  virtual bool equalTo(const TypeCtor &other) const;

private:
  QString _M_name;
};

class PointerTypeCtor: public TypeCtor
{
public:
  PointerTypeCtor();
  virtual ~PointerTypeCtor();

  virtual bool equalTo(const TypeCtor &other) const;
};

class ReferenceTypeCtor: public TypeCtor
{
public:
  ReferenceTypeCtor();
  virtual ~ReferenceTypeCtor();

  virtual bool equalTo(const TypeCtor &other) const;
};

class NamedTypeCtor: public TypeCtor
{
public:
  NamedTypeCtor();
  virtual ~NamedTypeCtor();

  QString name() const;
  void setName(const QString &name);

  virtual bool equalTo(const TypeCtor &other) const;

private:
  QString _M_name;
};

class ArrayTypeCtor: public TypeCtor
{
public:
  ArrayTypeCtor();
  virtual ~ArrayTypeCtor();

  int size() const;
  void setSize(int size);

  virtual bool equalTo(const TypeCtor &other) const;

private:
  int _M_size;
};

class ConstTypeCtor: public TypeCtor
{
public:
  ConstTypeCtor();
  virtual ~ConstTypeCtor();

  virtual bool equalTo(const TypeCtor &other) const;
};

class VolatileTypeCtor: public TypeCtor
{
public:
  VolatileTypeCtor();
  virtual ~VolatileTypeCtor();

  virtual bool equalTo(const TypeCtor &other) const;
};

class MemberTypeCtor: public TypeCtor
{
public:
  MemberTypeCtor();
  virtual ~MemberTypeCtor();

  QString name() const;
  void setName(const QString &name);

  virtual bool equalTo(const TypeCtor &other) const;

private:
  QString _M_name;
};

class FunctionTypeCtor: public TypeCtor
{
public:
  FunctionTypeCtor();
  virtual ~FunctionTypeCtor();

  TypeVariable returnType() const;
  void setReturnType(const TypeVariable &returnType);

  QList<TypeVariable> arguments() const;
  void clearArguments();
  void setArguments(const QList<TypeVariable> &arguments);
  void addArgument(const TypeVariable &argument);

  virtual bool equalTo(const TypeCtor &other) const;

private:
  TypeVariable _M_returnType;
  QList<TypeVariable> _M_arguments;
};

class TemplateTypeCtor: public TypeCtor
{
public:
  TemplateTypeCtor();
  virtual ~TemplateTypeCtor();

  QList<TypeVariable> arguments() const;
  void clearArguments();
  void setArguments(const QList<TypeVariable> &arguments);
  void addArgument(const TypeVariable &argument);

  virtual bool equalTo(const TypeCtor &other) const;

private:
  QList<TypeVariable> _M_arguments;
};


// ### remove me!! QHash/QSet I hate you :-)
uint qHash(const PrimitiveTypeCtor &ctor);
uint qHash(const ArrayTypeCtor &ctor);
uint qHash(const NamedTypeCtor &ctor);
uint qHash(const MemberTypeCtor &ctor);
uint qHash(const FunctionTypeCtor &ctor);
uint qHash(const TemplateTypeCtor &ctor);

} // namespace rpp

#endif // RPP_TYPE_CTORS_H

// kate: indent-width 2;
