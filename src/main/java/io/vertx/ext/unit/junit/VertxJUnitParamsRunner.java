package io.vertx.ext.unit.junit;

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
    
    private JUnitParamsRunner junitParamsRunner;

	private ParametrizedTestMethodsFilter parametrizedTestMethodsFilter;
    private ParameterisedTestClassRunner parameterisedRunner;
    private Description description;

    public VertxJUnitParamsRunner(Class<?> klass) throws InitializationError {
        super(klass);
        parameterisedRunner = new ParameterisedTestClassRunner(getTestClass());
        junitParamsRunner = new JUnitParamsRunner(klass);
        parametrizedTestMethodsFilter = new ParametrizedTestMethodsFilter(junitParamsRunner);
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
            verifyMethodCanBeRunByStandardRunner(testMethod);
            super.runChild(method, notifier);
        }
    }

    private void verifyMethodCanBeRunByStandardRunner(TestMethod testMethod) {
        List<Throwable> errors = new ArrayList<Throwable>();
        testMethod.frameworkMethod().validatePublicVoidNoArg(false, errors);
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
        Statement methodInvoker = parameterisedRunner.parameterisedMethodInvoker(method, test);
        if (methodInvoker == null)
            methodInvoker = super.methodInvoker(method, test);

        return methodInvoker;
    }

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