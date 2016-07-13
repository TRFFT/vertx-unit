package io.vertx.ext.unit.junit;

import io.vertx.ext.unit.impl.TestContextImpl;
import junitparams.JUnitParamsRunner;
import junitparams.internal.ParameterisedTestClassRunner;
import junitparams.internal.ParametrizedTestMethodsFilter;
import junitparams.internal.TestMethod;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;



public class VertxJUnitParamsRunner extends VertxUnitRunner {
    
//    private final Object[] parameters;
//    private final String name;
    
    private JUnitParamsRunner junitParamsRunner;

	private ParametrizedTestMethodsFilter parametrizedTestMethodsFilter;
    private ParameterisedTestClassRunner parameterisedRunner;
    private Description description;

    public VertxJUnitParamsRunner(Class<?> klass) throws InitializationError {
        super(klass);
        parameterisedRunner = new ParameterisedTestClassRunner(getTestClass());
        junitParamsRunner = new JUnitParamsRunner(klass);
        parametrizedTestMethodsFilter = new ParametrizedTestMethodsFilter(junitParamsRunner);
//        parameters = test.getParameters().toArray(new Object[test.getParameters().size()]);
//            name = test.getName();
    }
    
    @Override
    public void filter(Filter filter) throws NoTestsRemainException {
        super.filter(filter);
        this.parametrizedTestMethodsFilter = new ParametrizedTestMethodsFilter(junitParamsRunner,filter);
    }

    @Override
    protected void collectInitializationErrors(List<Throwable> errors) {
        super.validateFields(errors);
        for (Throwable throwable : errors)
            throwable.printStackTrace();
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        if (handleIgnored(method, notifier))
            return;

        TestMethod testMethod = parameterisedRunner.testMethodFor(method);
        if (parameterisedRunner.shouldRun(testMethod)){
            parameterisedRunner.runParameterisedTest(testMethod, methodBlock(method), notifier);
        }
        else{
            verifyMethodCanBeRunByVertxRunner(testMethod);
            super.runChild(method, notifier);
        }
    }
    
    private void verifyMethodCanBeRunByVertxRunner(TestMethod testMethod) {
        List<Throwable> errors = new ArrayList<Throwable>();
        testMethod.frameworkMethod().validatePublicVoid(false, errors);
        if (!errors.isEmpty()) {
            throw new RuntimeException(errors.get(0));
        }
    }

    private boolean handleIgnored(FrameworkMethod method, RunNotifier notifier) {
        TestMethod testMethod = parameterisedRunner.testMethodFor(method);
        if (testMethod.isIgnored())
            notifier.fireTestIgnored(describeMethod(method));

        return testMethod.isIgnored();
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        return parameterisedRunner.computeFrameworkMethods();
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        TestContextImpl ctx = testContext;
        // TODO: Special VertxParameterisedRunner
        Statement methodInvoker = parameterisedRunner.parameterisedMethodInvoker(method, test);
        if (methodInvoker == null) { // not-parameterized test
            // Invoke normal Vertx Unit method
            methodInvoker = new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    invokeExplosively(ctx, method, test);
                }; 
            };
//            methodInvoker = super.methodInvoker(method, test);
        }
        return methodInvoker; 
    }
    
//    private Statement buildMethodInvoker(FrameworkMethod method, Object testClass, TestMethod testMethod) {
//        ParameterisedTestMethodRunner parameterisedMethod = parameterisedMethods.get(testMethod);
//
//        return new InvokeParameterisedMethod(
//                method, testClass, parameterisedMethod.currentParamsFromAnnotation(), parameterisedMethod.count());
//    }

    @Override
    public Description getDescription() {
        if (description == null) {
            description = Description.createSuiteDescription(getName(), getTestClass().getAnnotations());
            List<FrameworkMethod> resultMethods = getListOfMethods();

            for (FrameworkMethod method : resultMethods)
                description.addChild(describeMethod(method));
        }

        return description;
    }

    private List<FrameworkMethod> getListOfMethods() {
        List<FrameworkMethod> frameworkMethods = parameterisedRunner.returnListOfMethods();
        return parametrizedTestMethodsFilter.filteredMethods(frameworkMethods);
    }

    public Description describeMethod(FrameworkMethod method) {
        Description child = parameterisedRunner.describeParameterisedMethod(method);

        if (child == null)
            child = describeChild(method);

        return child;
    }
}